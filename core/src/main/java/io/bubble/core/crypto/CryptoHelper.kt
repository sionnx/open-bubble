package io.bubble.core.crypto

import com.oplus.wearable.crypto.AESCipher

/**
 * 对应 LinkService {@code fk.a SecurityManager} 在 Consult 路径上的加解密接口。
 */
object CryptoHelper {
    private val cipher = AESCipher()

    fun deriveIdentityKey(localRandom: Long, remoteRandom: Long): ByteArray? =
        cipher.deriveIdentityKey(localRandom, remoteRandom)

    fun encryptAes128(data: ByteArray, key: ByteArray): ByteArray? =
        cipher.encryptAes128(data, key)

    fun decryptAes128(cipherData: ByteArray, key: ByteArray): ByteArray? =
        cipher.decryptAes128(cipherData, key)

    fun encryptAes128NoIv(data: ByteArray, key: ByteArray): ByteArray? =
        encryptAes128(data, key)

    fun decryptAes128NoIv(cipherData: ByteArray, key: ByteArray): ByteArray? =
        decryptAes128(cipherData, key)

    fun decryptAes256(cipherData: ByteArray, key: ByteArray, iv: ByteArray?): ByteArray? =
        cipher.decryptAes256(cipherData, key, iv)
}
