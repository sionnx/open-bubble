package io.bubble.core.protocol

/** 对应 {@code nj.a}。 */
object ConnectionTypeHelper {
    fun isKeyConsultType(connectionType: Int): Boolean =
        connectionType == 1 || connectionType == 5

    fun isIdentityConsultType(connectionType: Int): Boolean =
        connectionType == 3 || connectionType == 2

    fun isAlternateIdentityType(connectionType: Int): Boolean =
        connectionType == 1 || connectionType == 4

    fun isBleType(connectionType: Int): Boolean =
        connectionType == 2
}
