package io.bubble.core.scan

/**
 * 对齐 {@code com.oplus.ebadge.discovery.d}（DeviceScanController）Companion 常量。
 */
object EbadgeScanConstants {
    /** Manufacturer ID 1946 (0x079A)，OPPO 电子徽章广播。 */
    const val MANUFACTURER_ID_EBADGE = 1946

    /** {@code MANUFACTURER_DATA_PREFIX} = {8, 16} */
    val MANUFACTURER_DATA_PREFIX: ByteArray = byteArrayOf(8, 16)

    /** {@code MANUFACTURER_DATA_MASK} = {0xFF, 0xFF} */
    val MANUFACTURER_DATA_MASK: ByteArray = byteArrayOf(0xFF.toByte(), 0xFF.toByte())

    /** 单次 BLE 扫描时长（DEX 中 {@code discovery/d} 方法 {@code a} 使用 15000）。 */
    const val SCAN_PERIOD_MS = 15_000L

    /** {@code SCAN_FAILED_SCANNER_UNAVAILABLE}，scanner 为 null 时上报。 */
    const val SCAN_FAILED_SCANNER_UNAVAILABLE = -1

    /** {@code ScanSettings.SCAN_MODE_LOW_LATENCY} */
    const val SCAN_MODE = 2
}
