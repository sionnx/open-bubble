package io.bubble.core.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import io.bubble.core.protocol.TransportLayer
import java.io.IOException
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

/**
 * 经典蓝牙 RFCOMM，对应 {@code nk.a} + {@code kk.b}。
 */
class RfcommTransport(
    private val scope: CoroutineScope,
    private val onConsultPacket: (ByteArray) -> Unit,
    private val onDisconnected: (Throwable?) -> Unit,
) {
    private var socket: BluetoothSocket? = null
    private var readJob: Job? = null
    private val running = AtomicBoolean(false)
    private val transportLayer = TransportLayer(maxMtu = 20)

    @Throws(IOException::class)
    fun connect(device: BluetoothDevice, uuid: UUID) {
        closeQuietly()
        val connected = tryConnect(device, uuid)
        if (!connected) {
            throw IOException("RFCOMM connect failed for ${device.address}")
        }
        running.set(true)
        readJob = scope.launch(Dispatchers.IO) {
            readLoop()
        }
    }

    @Synchronized
    fun sendConsultFrames(frames: List<ByteArray>) {
        val out = socket?.outputStream ?: throw IOException("socket not connected")
        frames.forEach { frame ->
            out.write(frame)
            out.flush()
        }
    }

    /** @param body 仅 protobuf 载荷（不含 sid/cid 前缀）。 */
    fun sendConsultCommand(sid: Int, cid: Int, body: ByteArray) {
        val frames = transportLayer.packConsultPayload(sid, cid, body)
        sendConsultFrames(frames)
    }

    /** 发送完整 Consult 帧（sid + cid + protobuf），与 {@code pk.a#G} 一致。 */
    fun sendConsultFrame(frame: ByteArray) {
        if (frame.size < 2) return
        val sid = frame[0].toInt() and 0xFF
        val cid = frame[1].toInt() and 0xFF
        sendConsultCommand(sid, cid, frame.copyOfRange(2, frame.size))
    }

    fun close() {
        running.set(false)
        readJob?.cancel()
        readJob = null
        closeQuietly()
        onDisconnected(null)
    }

    private fun tryConnect(device: BluetoothDevice, uuid: UUID): Boolean {
        try {
            val s = device.createRfcommSocketToServiceRecord(uuid)
            s.connect()
            socket = s
            logcat(LogPriority.DEBUG) { "connected via service record $uuid" }
            return true
        } catch (first: IOException) {
            logcat(LogPriority.ERROR) { "service record failed\n${first.asLog()}" }
        }
        for (channel in 1..4) {
            try {
                val method = device.javaClass.getMethod(
                    "createRfcommSocket",
                    Int::class.javaPrimitiveType,
                )
                val s = method.invoke(device, channel) as BluetoothSocket
                s.connect()
                socket = s
                logcat(LogPriority.DEBUG) { "connected via fallback channel $channel" }
                return true
            } catch (e: Exception) {
                logcat(LogPriority.ERROR) { "fallback channel $channel failed\n${e.asLog()}" }
            }
        }
        return false
    }

    private fun readLoop() {
        val input = socket?.inputStream ?: return
        val buf = ByteArray(4096)
        try {
            while (running.get() && scope.isActive) {
                val read = input.read(buf)
                if (read <= 0) break
                val chunk = buf.copyOf(read)
                transportLayer.feed(chunk).forEach { payload ->
                    if (payload.size >= 2) {
                        onConsultPacket(payload)
                    }
                }
            }
        } catch (e: Exception) {
            if (running.get()) {
                logcat(LogPriority.ERROR) { "read loop error\n${e.asLog()}" }
                onDisconnected(e)
            }
        } finally {
            running.set(false)
            closeQuietly()
        }
    }

    private fun closeQuietly() {
        try {
            socket?.close()
        } catch (e: IOException) {
            logcat(LogPriority.ERROR) { "close socket failed\n${e.asLog()}" }
        }
        socket = null
    }
}
