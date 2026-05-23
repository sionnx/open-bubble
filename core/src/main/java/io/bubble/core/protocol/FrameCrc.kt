package io.bubble.core.protocol

/** 对应 {@code ek.a}：Consult 传输层 CRC16。 */
object FrameCrc {
    fun compute(data: ByteArray, start: Int, end: Int): Int {
        if (end > data.size) return 0
        if (start >= end) return 0
        var crc = 0
        var index = start
        while (index < end) {
            crc = crc xor (data[index].toInt() shl 8)
            repeat(8) {
                crc = if (crc and 0x8000 != 0) {
                    (crc shl 1) xor 0x1021
                } else {
                    crc shl 1
                }
            }
            index++
        }
        return crc and 0xFFFF
    }
}
