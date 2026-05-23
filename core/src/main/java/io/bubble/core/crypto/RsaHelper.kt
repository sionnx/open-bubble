package io.bubble.core.crypto

import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import javax.crypto.Cipher

/** 对应 LinkService {@code tk.a}，使用标准 JCA。 */
object RsaHelper {
    private const val RSA_BITS = 2048

    data class RsaKeyPair(val publicKey: RSAPublicKey, val privateKey: RSAPrivateKey)

    fun generateKeyPair(): RsaKeyPair {
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(RSA_BITS)
        val pair = generator.generateKeyPair()
        return RsaKeyPair(
            publicKey = pair.public as RSAPublicKey,
            privateKey = pair.private as RSAPrivateKey,
        )
    }

    fun decrypt(cipher: ByteArray, privateKeyEncoded: ByteArray): ByteArray? {
        return try {
            val cipherInst = Cipher.getInstance("RSA/NONE/PKCS1Padding")
            val privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(java.security.spec.PKCS8EncodedKeySpec(privateKeyEncoded))
            cipherInst.init(Cipher.DECRYPT_MODE, privateKey)
            cipherInst.doFinal(cipher)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR) { "decrypt failed\n${e.asLog()}" }
            null
        }
    }

    fun decryptWithPrivate(cipher: ByteArray, privateKey: RSAPrivateKey): ByteArray? {
        return try {
            val cipherInst = Cipher.getInstance("RSA/NONE/PKCS1Padding")
            cipherInst.init(Cipher.DECRYPT_MODE, privateKey)
            cipherInst.doFinal(cipher)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR) { "decryptWithPrivate failed\n${e.asLog()}" }
            null
        }
    }
}
