package io.bubble.core.crypto

/** 对应 {@code sk.h#a}。 */
object BitInvert {
    fun invert(data: ByteArray?): ByteArray? {
        if (data == null) return null
        return ByteArray(data.size) { i -> (data[i].toInt() xor 0xFF).toByte() }
    }
}
