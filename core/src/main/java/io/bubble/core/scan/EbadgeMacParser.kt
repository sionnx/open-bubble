package io.bubble.core.scan

import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import java.util.Locale

/**
 * 复刻 {@code jf.a1}：从 BLE 广播厂商数据解析真实经典蓝牙 MAC。
 *
 * 勿使用 {@link ScanResult#getDevice()} {@code getAddress()}，与配对用 MAC 可能不一致。
 */
object EbadgeMacParser {
    fun parseRealMac(scanResult: ScanResult?): String? {
        if (scanResult == null) return null
        val record: ScanRecord = scanResult.scanRecord ?: return null
        val manufacturer = record.getManufacturerSpecificData(EbadgeScanConstants.MANUFACTURER_ID_EBADGE)
            ?: return null
        if (manufacturer.size < 6) return null
        return formatMac(
            manufacturer.copyOfRange(manufacturer.size - 6, manufacturer.size),
        )
    }

    fun normalizeMac(mac: String): String = mac.trim().uppercase(Locale.ROOT)

    private fun formatMac(lastSix: ByteArray): String? {
        if (lastSix.size != 6) return null
        return buildString {
            for (i in lastSix.indices) {
                if (i > 0) append(':')
                append(String.format(Locale.ROOT, "%02X", lastSix[i].toInt() and 0xFF))
            }
        }
    }
}
