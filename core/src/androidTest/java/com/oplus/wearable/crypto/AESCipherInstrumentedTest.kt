package com.oplus.wearable.crypto

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Arrays

/**
 * 验证 [libencrypt.so] 在 Android 进程内可加载且 JNI 加解密与官方行为一致。
 *
 * 仅打包了 [arm64-v8a] 的 .so，需在 arm64 真机或模拟器上运行：
 * `./gradlew :core:connectedDebugAndroidTest`
 */
@RunWith(AndroidJUnit4::class)
class AESCipherInstrumentedTest {

    private lateinit var cipher: AESCipher

    @Before
    fun setUp() {
        assumeTrue(
            "libencrypt.so 仅提供 arm64-v8a，当前 ABI=${Build.SUPPORTED_ABIS.joinToString()}",
            Build.SUPPORTED_ABIS.any { it == "arm64-v8a" },
        )
        cipher = AESCipher()
    }

    @Test
    fun deriveIdentityKey_isDeterministicAnd16Bytes() {
        val local = 1_704_067_200_000L
        val remote = 1_704_067_201_000L

        val first = cipher.deriveIdentityKey(local, remote)
        val second = cipher.deriveIdentityKey(local, remote)

        assertNotNull(first)
        assertNotNull(second)
        assertEquals(16, first!!.size)
        assertArrayEquals(first, second)

        val swapped = cipher.deriveIdentityKey(remote, local)
        assertNotNull(swapped)
        assertTrue(!Arrays.equals(first, swapped!!))
    }

    @Test
    fun deriveIdentityKey_matchesKnownVector() {
        // 在 arm64 + libencrypt.so 上录制的固定向量，用于回归 native 是否被替换
        val key = cipher.deriveIdentityKey(1_704_067_200_000L, 1_704_067_201_000L)
        assertNotNull(key)
        assertArrayEquals(IDENTITY_KEY_GOLDEN, key)
    }

    @Test
    fun aes128_roundTrip_variousLengths() {
        val key = cipher.deriveIdentityKey(100L, 200L)!!
        val payloads = listOf(
            byteArrayOf(0x01),
            "hello".toByteArray(),
            ByteArray(15) { it.toByte() },
            ByteArray(16) { (it + 1).toByte() },
            ByteArray(32) { (it * 3).toByte() },
        )
        for (plain in payloads) {
            val encrypted = cipher.encryptAes128(plain, key)
            assertNotNull("encrypt failed len=${plain.size}", encrypted)
            val decrypted = cipher.decryptAes128(encrypted!!, key)
            assertArrayEquals("round-trip len=${plain.size}", plain, decrypted)
        }
    }

    @Test
    fun aes256_roundTrip_withIv() {
        val key = ByteArray(32) { (it + 0x10).toByte() }
        val iv = ByteArray(16) { (it + 0x20).toByte() }
        val payloads = listOf(
            "consult-payload".toByteArray(),
            ByteArray(16) { it.toByte() },
            ByteArray(20) { (it + 5).toByte() },
        )
        for (plain in payloads) {
            val encrypted = cipher.encryptAes256(plain, key, iv)
            assertNotNull("encrypt256 failed len=${plain.size}", encrypted)
            assertTrue(encrypted!!.isNotEmpty())
            val decrypted = cipher.decryptAes256(encrypted, key, iv)
            assertArrayEquals("aes256 round-trip len=${plain.size}", plain, decrypted)
        }
    }

    @Test
    fun aes128_wrongKey_doesNotRecoverPlaintext() {
        val plain = byteArrayOf(0x0A, 0x0B, 0x0C, 0x0D)
        val key = cipher.deriveIdentityKey(1L, 2L)!!
        val encrypted = cipher.encryptAes128(plain, key)!!
        val wrongKey = key.copyOf().also { it[0] = (it[0].toInt() xor 0xFF).toByte() }
        val decrypted = cipher.decryptAes128(encrypted, wrongKey)
        assertTrue(decrypted == null || !decrypted.contentEquals(plain))
    }

    @Test(expected = IllegalArgumentException::class)
    fun aes128_rejectsNullKey() {
        cipher.encryptAes128(byteArrayOf(1), null)
    }

    companion object {
        /** arm64 libencrypt.so：deriveIdentityKey(1704067200000, 1704067201000) */
        private val IDENTITY_KEY_GOLDEN = byteArrayOf(
            0xFB.toByte(), 0x40, 0xEB.toByte(), 0xDF.toByte(), 0xB7.toByte(), 0x40, 0x7A, 0x14,
            0x57, 0xAD.toByte(), 0x5D, 0xE1.toByte(), 0x20, 0x3C, 0x2A, 0x34,
        )
    }
}
