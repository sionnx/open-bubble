package io.bubble.core.scan

/**
 * 扫描列表项：MAC + 展示名（对齐官方 Pair&lt;String, String&gt;）。
 */
data class ScannedDevice(
    val mac: String,
    val displayName: String,
)
