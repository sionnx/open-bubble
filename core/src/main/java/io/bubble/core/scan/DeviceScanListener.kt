package io.bubble.core.scan

/**
 * 对齐 {@code com.oplus.ebadge.discovery.d.b}（DeviceScanController.Listener）。
 */
interface DeviceScanListener {
    /** 已绑定设备列表刷新（MAC 可空，与官方 Pair 一致）。 */
    fun onBoundDevicesLoaded(devices: List<ScannedDevice>) {}

    /** 扫描开始。 */
    fun onScanStarted() {}

    /** 扫描结束（超时或主动 stop）。 */
    fun onScanStopped() {}

    /** 附近未绑定设备列表更新。 */
    fun onNearbyDevicesChanged(devices: List<ScannedDevice>) {}

    /** 扫描失败，{@link EbadgeScanConstants#SCAN_FAILED_SCANNER_UNAVAILABLE} 等。 */
    fun onScanFailed(errorCode: Int) {}

    /**
     * 请求宿主处理权限；官方传空数组，由 UI 层弹窗。
     * 完成后必须调用 [onResult]。
     */
    fun onRequestPermissions(
        permissions: Array<String>,
        onResult: (allGranted: Boolean) -> Unit,
    ) {
        onResult(false)
    }
}
