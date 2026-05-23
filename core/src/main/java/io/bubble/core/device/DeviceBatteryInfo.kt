package io.bubble.core.device

/**
 * 设备电量快照，对应官方 `DMProto.BatteryInfo` + `UserDeviceInfo.capacityPercent`。
 *
 * @param percent 0–100 为有效电量；[UNKNOWN_PERCENT] 表示设备未上报或解析失败。
 */
data class DeviceBatteryInfo(
    val mac: String,
    val percent: Int,
    val isCharging: Boolean,
    val updatedAtMs: Long = System.currentTimeMillis(),
) {
    val isKnown: Boolean
        get() = percent in 0..100

    companion object {
        const val UNKNOWN_PERCENT = -1
    }
}

/**
 * 电量变更监听（含主动查询结果与设备主动上报）。
 */
fun interface DeviceBatteryListener {
    fun onBatteryUpdated(info: DeviceBatteryInfo)
}

/**
 * [DeviceBatteryClient.fetchBattery] 失败原因。
 */
enum class BatteryError {
    /** [BubbleConnectManager] 未 init 或未连接。 */
    NOT_CONNECTED,

    /** 等待应答超时（默认 5s，对齐官方 `kb.t.b` 默认）。 */
    TIMEOUT,

    /** 应答 protobuf 无法解析。 */
    PARSE_ERROR,

    /** 发送或 IO 异常。 */
    IO_ERROR,
}

class BatteryException(
    val error: BatteryError,
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
