package io.bubble.core.protocol

import java.util.UUID

/** 对应 {@code ik.f} 中 Wearable LinkService RFCOMM 服务 UUID。 */
object LinkUuids {
    val CONSULT_MAIN: UUID = UUID.fromString("00002760-08C2-11E1-9073-0E8AC72E1011")
    val CONSULT_ALT: UUID = UUID.fromString("00002760-08C2-11E1-9073-0E8AC72E0011")
    val DATA_MAIN: UUID = UUID.fromString("00002760-08C2-11E1-9073-0E8AC72E1012")
}
