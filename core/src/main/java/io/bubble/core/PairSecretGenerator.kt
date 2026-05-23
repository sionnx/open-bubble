package io.bubble.core

import kotlin.random.Random

/** 对应 {@code kb.s}：16 位随机配对密钥。 */
object PairSecretGenerator {
    private const val ALPHABET =
        "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

    fun generate(): String = buildString(16) {
        repeat(16) {
            append(ALPHABET[Random.nextInt(ALPHABET.length)])
        }
    }
}
