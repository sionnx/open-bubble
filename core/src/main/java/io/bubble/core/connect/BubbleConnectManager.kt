package io.bubble.core.connect

import android.content.Context
import io.bubble.core.PairSecretGenerator
import io.bubble.core.bluetooth.BondHelper
import io.bubble.core.bluetooth.RfcommTransport
import io.bubble.core.bluetooth.bluetoothAdapter
import io.bubble.core.device.DeviceBatteryClient
import io.bubble.core.protocol.ConsultClient
import io.bubble.core.protocol.LinkUuids
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logcat.AndroidLogcatLogger
import logcat.LogcatLogger
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

/**
 * 复刻 OPPO Bubble Debug「连接测试」链路：
 *
 * DebugActivity.connect → managerApi.H → HDM2Connect.H/m4(pair=true)
 * → NodeApi.t → LinkService createBond → RFCOMM + Consult。
 *
 * 仅使用 Android 蓝牙 API + 协议层（无 HeyTap/OAF/LinkService Java SDK）。
 */
object BubbleConnectManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val listeners = CopyOnWriteArrayList<ConnectListener>()
    private val packetListeners = CopyOnWriteArrayList<(ByteArray) -> Unit>()
    private val mutex = Mutex()

    private var appContext: Context? = null
    private var connectJob: Job? = null
    private val activeMac = AtomicReference<String?>(null)
    private val activeState = AtomicReference(ConnectState.IDLE)
    private var transport: RfcommTransport? = null
    private var consultClient: ConsultClient? = null
    private var lastConfig: TransferConfig? = null

    fun init(context: Context) {
        appContext = context.applicationContext
        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install()
        }
        if (LogcatLogger.loggers.none { it is AndroidLogcatLogger }) {
            LogcatLogger.loggers += AndroidLogcatLogger(LogPriority.VERBOSE)
        }
        DeviceBatteryClient.registerWithConnectManager()
    }

    /** 订阅 RFCOMM 业务报文（sid/cid + protobuf），供电量等模块使用。 */
    fun addPacketListener(listener: (ByteArray) -> Unit) {
        packetListeners.addIfAbsent(listener)
    }

    fun removePacketListener(listener: (ByteArray) -> Unit) {
        packetListeners.remove(listener)
    }

    /**
     * 发送业务消息（需已 Consult 成功）。对应官方 `messageApi.a(MessageEvent)`。
     */
    fun sendBusinessMessage(sid: Int, cid: Int, body: ByteArray): Result<Unit> {
        val rfcomm = transport
            ?: return Result.failure(IllegalStateException("not connected"))
        if (activeState.get() != ConnectState.CONNECTED) {
            return Result.failure(IllegalStateException("connect state is ${activeState.get()}"))
        }
        return try {
            rfcomm.sendConsultCommand(sid, cid, body)
            Result.success(Unit)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR) { "send business message failed sid=$sid cid=$cid\n${e.asLog()}" }
            Result.failure(e)
        }
    }

    fun addListener(listener: ConnectListener) {
        listeners.addIfAbsent(listener)
    }

    fun removeListener(listener: ConnectListener) {
        listeners.remove(listener)
    }

    fun getState(): ConnectState = activeState.get()

    fun getActiveMac(): String? = activeMac.get()

    fun getLastTransferConfig(): TransferConfig? = lastConfig

    /**
     * 对应 {@code HDM2Connect.H} / {@code connectDeviceByPair}。
     */
    fun connectDeviceByPair(params: ConnectParams) {
        val ctx = appContext
        if (ctx == null) {
            notifyFailed(params.mac, -1, "BubbleConnectManager not initialized")
            return
        }
        connectJob?.cancel()
        connectJob = scope.launch {
            mutex.withLock {
                runConnect(ctx, params)
            }
        }
    }

    /**
     * 对应断开：NodeApi.a0 / WearableApiManager.disconnect。
     */
    fun disconnect(mac: String, reason: String = "disconnect") {
        connectJob?.cancel()
        scope.launch {
            mutex.withLock {
                transport?.close()
                transport = null
                consultClient = null
                DeviceBatteryClient.clearCache(mac)
                updateState(mac, ConnectState.DISCONNECTED, reason)
                if (activeMac.get().equals(mac, ignoreCase = true)) {
                    activeMac.set(null)
                }
            }
        }
    }

    private suspend fun runConnect(context: Context, params: ConnectParams) {
        val mac = params.mac.uppercase()
        activeMac.set(mac)
        if (!isBluetoothReady(context)) {
            notifyFailed(mac, 103, "bt no open")
            return
        }
        updateState(mac, ConnectState.CONNECTING, params.reason)
        try {
            val device = BondHelper.remoteDevice(context, mac)
            BondHelper.ensureBonded(context, device)
            val secret = params.deviceSecret?.toByteArray(Charsets.UTF_8)
                ?: PairSecretGenerator.generate().toByteArray(Charsets.UTF_8)
            val rfcomm = RfcommTransport(
                scope = scope,
                onConsultPacket = { frame ->
                    consultClient?.onFrame(frame)
                    dispatchPacket(frame)
                },
                onDisconnected = { error ->
                    if (error != null) {
                        notifyFailed(mac, 301, error.message ?: "socket closed")
                    }
                },
            )
            transport = rfcomm
            rfcomm.connect(device, LinkUuids.CONSULT_MAIN)
            val consult = ConsultClient(
                mac = mac,
                pairSecret = secret,
                connectionType = 2,
                send = { sid, cid, body -> rfcomm.sendConsultCommand(sid, cid, body) },
                onSuccess = { result ->
                    lastConfig = TransferConfig(
                        mtu = result.mtu,
                        maxFrameSize = result.maxFrameSize,
                        interval = result.interval,
                        supportEncrypt = result.supportEncrypt,
                    )
                    updateState(mac, ConnectState.CONNECTED, "consult ok")
                    listeners.forEach {
                        it.onConnected(mac, lastConfig!!)
                    }
                },
                onError = { code ->
                    notifyFailed(mac, code, "consult failed")
                    rfcomm.close()
                },
            )
            consultClient = consult
            consult.start()
        } catch (e: Exception) {
            logcat(LogPriority.ERROR) { "connect failed\n${e.asLog()}" }
            notifyFailed(mac, 103, e.message ?: "connect error")
            transport?.close()
            transport = null
            consultClient = null
        }
    }

    private fun isBluetoothReady(context: Context): Boolean {
        val adapter = bluetoothAdapter(context) ?: return false
        return adapter.isEnabled
    }

    private fun updateState(mac: String, state: ConnectState, detail: String?) {
        activeState.set(state)
        listeners.forEach { it.onStateChanged(mac, state, detail) }
    }

    private fun notifyFailed(mac: String, code: Int, detail: String) {
        logcat(LogPriority.ERROR) { "connection failed mac=$mac code=$code detail=$detail" }
        activeState.set(ConnectState.FAILED)
        listeners.forEach { it.onFailed(mac, code, detail) }
        listeners.forEach { it.onStateChanged(mac, ConnectState.FAILED, detail) }
    }

    private fun dispatchPacket(frame: ByteArray) {
        packetListeners.forEach { listener ->
            try {
                listener(frame)
            } catch (e: Exception) {
                logcat(LogPriority.ERROR) { "packet listener error\n${e.asLog()}" }
            }
        }
    }
}
