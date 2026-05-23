package io.bubble.core.device

import io.bubble.core.connect.BubbleConnectManager
import io.bubble.core.connect.ConnectState
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

/**
 * 设备电量对外 API，对齐官方 `hb.a.a`（sid=1 cid=8）+ `mb.c` 应答处理。
 *
 * 使用前须 [BubbleConnectManager.init] 且连接状态为 [ConnectState.CONNECTED]。
 *
 * ```kotlin
 * val info = DeviceBatteryClient.fetchBattery() // 挂起，默认 5s 超时
 * val cached = DeviceBatteryClient.getCachedBattery(mac)
 * DeviceBatteryClient.addListener { info -> … }
 * ```
 */
object DeviceBatteryClient {
    private const val DEFAULT_TIMEOUT_MS = 5_000L

    private val cache = ConcurrentHashMap<String, DeviceBatteryInfo>()
    private val listeners = CopyOnWriteArrayList<DeviceBatteryListener>()
    private val pending = java.util.concurrent.atomic.AtomicReference<PendingRequest?>(null)

    @Volatile
    private var registered = false

    private data class PendingRequest(
        val mac: String,
        val continuation: kotlinx.coroutines.CancellableContinuation<DeviceBatteryInfo>,
    )

    /**
     * 向已连接设备请求电量（阻塞至收到 sid=1 cid=8 应答或超时）。
     *
     * @param mac 目标 MAC；null 时使用 [BubbleConnectManager.getActiveMac]。
     * @param timeoutMs 等待应答毫秒数，默认 5000。
     */
    suspend fun fetchBattery(
        mac: String? = null,
        timeoutMs: Long = DEFAULT_TIMEOUT_MS,
    ): DeviceBatteryInfo {
        ensureRegistered()
        val targetMac = resolveMac(mac)
        if (BubbleConnectManager.getState() != ConnectState.CONNECTED) {
            throw BatteryException(BatteryError.NOT_CONNECTED, "device not connected")
        }
        val body = BatteryMessageCodec.encodeRequest(targetMac)
        val result = withTimeoutOrNull(timeoutMs) {
            suspendCancellableCoroutine { cont ->
                if (!pending.compareAndSet(null, PendingRequest(targetMac, cont))) {
                    cont.resumeWithException(
                        BatteryException(BatteryError.IO_ERROR, "another battery request in progress"),
                    )
                    return@suspendCancellableCoroutine
                }
                cont.invokeOnCancellation {
                    pending.compareAndSet(pending.get(), null)
                }
                val send = BubbleConnectManager.sendBusinessMessage(
                    BatteryMessageCodec.SID_DEVICE_MANAGER,
                    BatteryMessageCodec.CID_BATTERY,
                    body,
                )
                send.onFailure { error ->
                    pending.set(null)
                    cont.resumeWithException(
                        BatteryException(BatteryError.IO_ERROR, error.message ?: "send failed", error),
                    )
                }
            }
        }
        if (result == null) {
            pending.set(null)
            throw BatteryException(BatteryError.TIMEOUT, "battery response timeout (${timeoutMs}ms)")
        }
        return result
    }

    /**
     * 读取最近一次成功拉取/上报的缓存；未请求过则 null。
     */
    fun getCachedBattery(mac: String? = null): DeviceBatteryInfo? {
        val key = mac?.let { BatteryMessageCodec.normalizeMac(it) } ?: BubbleConnectManager.getActiveMac()
        return key?.let { cache[it] }
    }

    fun addListener(listener: DeviceBatteryListener) {
        listeners.addIfAbsent(listener)
    }

    fun removeListener(listener: DeviceBatteryListener) {
        listeners.remove(listener)
    }

    /** 清除内存缓存（断开连接时可由宿主调用）。 */
    fun clearCache(mac: String? = null) {
        if (mac == null) {
            cache.clear()
        } else {
            cache.remove(BatteryMessageCodec.normalizeMac(mac))
        }
    }

    internal fun onPacket(frame: ByteArray) {
        if (!BatteryMessageCodec.matchesBatteryResponse(frame)) return
        val body = BatteryMessageCodec.responseBody(frame) ?: return
        val info = BatteryMessageCodec.decodeResponse(body)
        if (info == null) {
            completePendingWithError(BatteryError.PARSE_ERROR, "invalid BatteryInfo protobuf")
            return
        }
        val normalized = info.copy(mac = BatteryMessageCodec.normalizeMac(info.mac))
        cache[normalized.mac] = normalized
        listeners.forEach { it.onBatteryUpdated(normalized) }
        completePendingSuccess(normalized)
    }

    internal fun registerWithConnectManager() {
        ensureRegistered()
    }

    private fun ensureRegistered() {
        if (registered) return
        synchronized(this) {
            if (registered) return
            BubbleConnectManager.addPacketListener(::onPacket)
            registered = true
        }
    }

    private fun resolveMac(mac: String?): String {
        val raw = mac ?: BubbleConnectManager.getActiveMac()
            ?: throw BatteryException(BatteryError.NOT_CONNECTED, "mac is null and no active connection")
        return BatteryMessageCodec.normalizeMac(raw)
    }

    private fun completePendingSuccess(info: DeviceBatteryInfo) {
        val request = pending.getAndSet(null) ?: return
        if (!request.continuation.isActive) return
        if (macMatches(request.mac, info.mac)) {
            request.continuation.resume(info)
        }
    }

    private fun completePendingWithError(error: BatteryError, message: String) {
        val request = pending.getAndSet(null) ?: return
        if (request.continuation.isActive) {
            request.continuation.resumeWithException(BatteryException(error, message))
        }
    }

    private fun macMatches(expected: String, actual: String): Boolean {
        return expected.equals(actual, ignoreCase = true) ||
            expected.replace(":", "").equals(actual.replace(":", ""), ignoreCase = true)
    }
}
