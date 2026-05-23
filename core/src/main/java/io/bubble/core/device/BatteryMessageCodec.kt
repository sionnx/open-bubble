package io.bubble.core.device

import java.nio.charset.StandardCharsets

/**
 * 编解码官方 sid=1 cid=8 电量协议：
 * - 请求：`com.op.proto.BatteryInfoRequester.BatteryInfoRequesterData`（field 1 = device_bt_mac）
 * - 应答：`com.heytap.health.protocol.dm.DMProto.BatteryInfo`（field 1/2/3）
 */
internal object BatteryMessageCodec {
    const val SID_DEVICE_MANAGER = 1
    const val CID_BATTERY = 8

    fun encodeRequest(deviceBtMac: String): ByteArray {
        val macBytes = deviceBtMac.toByteArray(StandardCharsets.UTF_8)
        val out = ByteArray(1 + varintSize(macBytes.size) + macBytes.size)
        var i = 0
        i = writeTag(out, i, 1, 2)
        i = writeVarint(out, i, macBytes.size)
        System.arraycopy(macBytes, 0, out, i, macBytes.size)
        return out
    }

    fun decodeResponse(body: ByteArray): DeviceBatteryInfo? {
        var percent = DeviceBatteryInfo.UNKNOWN_PERCENT
        var mac = ""
        var charging = false
        var index = 0
        while (index < body.size) {
            val tag = readVarint(body, index)
            if (tag == null) return null
            index = tag.second
            val field = (tag.first ushr 3).toInt()
            val wire = (tag.first and 0x7).toInt()
            when (field) {
                1 -> {
                    if (wire != 0) return null
                    val value = readVarint(body, index) ?: return null
                    percent = value.first.toInt()
                    index = value.second
                }
                2 -> {
                    if (wire != 2) return null
                    val len = readVarint(body, index) ?: return null
                    index = len.second
                    val end = index + len.first.toInt()
                    if (end > body.size) return null
                    mac = String(body, index, len.first.toInt(), StandardCharsets.UTF_8)
                    index = end
                }
                3 -> {
                    if (wire != 0) return null
                    val value = readVarint(body, index) ?: return null
                    charging = value.first != 0L
                    index = value.second
                }
                else -> {
                    index = skipField(body, index, wire) ?: return null
                }
            }
        }
        if (mac.isEmpty()) return null
        return DeviceBatteryInfo(
            mac = normalizeMac(mac),
            percent = percent,
            isCharging = charging,
        )
    }

    fun matchesBatteryResponse(frame: ByteArray): Boolean {
        if (frame.size < 2) return false
        val sid = frame[0].toInt() and 0xFF
        val cid = frame[1].toInt() and 0xFF
        return sid == SID_DEVICE_MANAGER && cid == CID_BATTERY
    }

    fun responseBody(frame: ByteArray): ByteArray? {
        if (!matchesBatteryResponse(frame) || frame.size < 3) return null
        return frame.copyOfRange(2, frame.size)
    }

    fun normalizeMac(mac: String): String =
        mac.trim().uppercase().replace('-', ':')

    private fun writeTag(out: ByteArray, offset: Int, field: Int, wire: Int): Int {
        return writeVarint(out, offset, (field shl 3) or wire)
    }

    private fun writeVarint(out: ByteArray, offset: Int, value: Int): Int {
        var v = value
        var i = offset
        while (true) {
            if ((v and -0x80) == 0) {
                out[i] = v.toByte()
                return i + 1
            }
            out[i] = ((v and 0x7F) or 0x80).toByte()
            v = v ushr 7
            i++
        }
    }

    private fun varintSize(value: Int): Int {
        var v = value
        var size = 0
        do {
            v = v ushr 7
            size++
        } while (v != 0)
        return size
    }

    private fun readVarint(data: ByteArray, offset: Int): Pair<Long, Int>? {
        var result = 0L
        var shift = 0
        var i = offset
        while (i < data.size && shift < 64) {
            val b = data[i].toInt() and 0xFF
            result = result or ((b and 0x7F).toLong() shl shift)
            i++
            if ((b and 0x80) == 0) {
                return result to i
            }
            shift += 7
        }
        return null
    }

    private fun skipField(data: ByteArray, offset: Int, wire: Int): Int? {
        return when (wire) {
            0 -> readVarint(data, offset)?.second
            1 -> if (offset + 8 <= data.size) offset + 8 else null
            2 -> {
                val len = readVarint(data, offset) ?: return null
                val end = len.second + len.first.toInt()
                if (end <= data.size) end else null
            }
            5 -> if (offset + 4 <= data.size) offset + 4 else null
            else -> null
        }
    }
}
