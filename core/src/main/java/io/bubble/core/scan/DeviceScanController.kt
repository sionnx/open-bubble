package io.bubble.core.scan

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.os.Looper
import java.util.Locale
import java.util.concurrent.CopyOnWriteArrayList
import logcat.AndroidLogcatLogger
import logcat.LogcatLogger
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

/**
 * 复刻 {@code com.oplus.ebadge.discovery.d}（DeviceScanController）：
 *
 * - Manufacturer 1946 + 前缀 {8,16} / 掩码 {FF,FF}
 * - {@link EbadgeMacParser} 解析真实 MAC
 * - 过滤已绑定 MAC、去重、15s 自动 stop
 * - 仅 Android BLE API，无 OEM / HeyTap SDK
 */
class DeviceScanController(
    context: Context,
    private val listener: DeviceScanListener,
    private val isActive: () -> Boolean = { true },
    private val boundMacProvider: (Context) -> List<String> = { BondedDeviceRegistry.loadBondedMacs(it) },
    private val defaultDeviceName: (Context, Int, Int) -> String = { ctx, index, total ->
        defaultDisplayName(ctx, index, total)
    },
) {
    private val appContext = context.applicationContext
    init {
        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install()
        }
        if (LogcatLogger.loggers.none { it is AndroidLogcatLogger }) {
            LogcatLogger.loggers += AndroidLogcatLogger(LogPriority.VERBOSE)
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val nearbyDevices = CopyOnWriteArrayList<ScannedDevice>()
    private val scanSettings: ScanSettings = ScanSettings.Builder()
        .setScanMode(EbadgeScanConstants.SCAN_MODE)
        .build()

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var boundMacSet: Set<String> = emptySet()
    private var isScanning = false

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val rawMac = EbadgeMacParser.parseRealMac(result) ?: return
            val mac = EbadgeMacParser.normalizeMac(rawMac)
            if (mac.isBlank() || boundMacSet.contains(mac)) return
            handler.post { addNearbyDevice(mac) }
        }

        override fun onScanFailed(errorCode: Int) {
            handler.post {
                logcat(LogPriority.ERROR) { "scan failed errorCode=$errorCode" }
                if (isActive()) {
                    listener.onScanFailed(errorCode)
                }
            }
        }
    }

    private val stopScanRunnable = Runnable { stopScan() }

    /** 对齐 {@code m()}：加载已绑定设备并通知 UI。 */
    fun loadBoundDevices() {
        val macs = boundMacProvider(appContext)
        boundMacSet = macs.map { EbadgeMacParser.normalizeMac(it) }.toSet()
        val devices = macs.mapIndexed { index, mac ->
            ScannedDevice(
                mac = mac,
                displayName = defaultDeviceName(appContext, index + 1, macs.size),
            )
        }
        if (isActive()) {
            listener.onBoundDevicesLoaded(devices)
        }
        logcat(LogPriority.DEBUG) { "loadBoundDevices -> count=${devices.size}" }
    }

    /**
     * 对齐 {@code q()}：先走权限回调，通过后启动扫描。
     * 官方传空 permissions，由宿主弹窗；此处默认检查 [BluetoothScanPermissions]。
     */
    fun requestScan() {
        listener.onRequestPermissions(emptyArray()) { granted ->
            if (granted && isActive() && BluetoothScanPermissions.hasScanPermissions(appContext)) {
                startScanInternal()
            }
        }
    }

    /** 在宿主已确认权限时直接开始扫描。 */
    fun startScan() {
        if (!BluetoothScanPermissions.hasScanPermissions(appContext)) {
            logcat(LogPriority.ERROR) { "startScan failed: missing scan permission" }
            listener.onScanFailed(EbadgeScanConstants.SCAN_FAILED_SCANNER_UNAVAILABLE)
            return
        }
        startScanInternal()
    }

    /** 对齐 {@code r()}：停止 BLE 扫描。 */
    @SuppressLint("MissingPermission")
    fun stopScan() {
        handler.removeCallbacks(stopScanRunnable)
        if (!isScanning) return
        isScanning = false
        val scanner = bluetoothAdapter?.bluetoothLeScanner
        if (scanner != null) {
            runCatching { scanner.stopScan(scanCallback) }
                .onFailure { logcat(LogPriority.ERROR) { "stopScan failed\n${it.asLog()}" } }
        }
        if (isActive()) {
            listener.onScanStopped()
        }
        logcat(LogPriority.DEBUG) { "stopScan" }
    }

    fun release() {
        stopScan()
        handler.removeCallbacksAndMessages(null)
    }

    fun getNearbyDevices(): List<ScannedDevice> = nearbyDevices.toList()

    @SuppressLint("MissingPermission")
    private fun startScanInternal() {
        resolveBluetoothAdapter()
        if (isScanning) {
            stopScan()
        }
        val scanner: BluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
            ?: run {
                logcat(LogPriority.ERROR) { "startScan failed: scanner unavailable" }
                listener.onScanFailed(EbadgeScanConstants.SCAN_FAILED_SCANNER_UNAVAILABLE)
                return
            }
        isScanning = true
        nearbyDevices.clear()
        listener.onNearbyDevicesChanged(emptyList())
        listener.onScanStarted()
        scanner.startScan(
            listOf(buildScanFilter()),
            scanSettings,
            scanCallback,
        )
        logcat(LogPriority.DEBUG) { "startScan" }
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(stopScanRunnable, EbadgeScanConstants.SCAN_PERIOD_MS)
    }

    private fun resolveBluetoothAdapter() {
        val manager = appContext.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        bluetoothAdapter = manager?.adapter
    }

    private fun addNearbyDevice(mac: String) {
        if (!isActive()) return
        if (nearbyDevices.any { it.mac == mac }) return
        val index = nearbyDevices.size + 1
        val device = ScannedDevice(
            mac = mac,
            displayName = defaultDeviceName(appContext, index, index),
        )
        nearbyDevices.add(device)
        logcat(LogPriority.DEBUG) { "onScanResult -> mac=$mac nearby=${nearbyDevices.size}" }
        listener.onNearbyDevicesChanged(nearbyDevices.toList())
    }

    companion object {
        fun buildScanFilter(): ScanFilter {
            return ScanFilter.Builder()
                .setManufacturerData(
                    EbadgeScanConstants.MANUFACTURER_ID_EBADGE,
                    EbadgeScanConstants.MANUFACTURER_DATA_PREFIX,
                    EbadgeScanConstants.MANUFACTURER_DATA_MASK,
                )
                .build()
        }

        fun defaultDisplayName(context: Context, indexOneBased: Int, totalCount: Int): String {
            val base = context.applicationInfo.loadLabel(context.packageManager).toString()
            return if (totalCount <= 1 || indexOneBased == 1) {
                base
            } else {
                "$base#$indexOneBased"
            }
        }

        /**
         * 便捷监听器：仅在 [isActive] 为 true 时分发（对齐官方 Companion.createListener）。
         */
        fun forwardingListener(
            isActive: () -> Boolean,
            onBoundDevicesLoaded: (List<ScannedDevice>) -> Unit,
            onNearbyDevicesChanged: (List<ScannedDevice>) -> Unit,
            onScanFailed: (Int) -> Unit,
            onRequestPermissions: ((
                Array<String>,
                (Boolean) -> Unit,
            ) -> Unit)? = null,
            onScanStarted: (() -> Unit)? = null,
            onScanStopped: (() -> Unit)? = null,
        ): DeviceScanListener {
            return object : DeviceScanListener {
                override fun onBoundDevicesLoaded(devices: List<ScannedDevice>) {
                    if (isActive()) onBoundDevicesLoaded(devices)
                }

                override fun onNearbyDevicesChanged(devices: List<ScannedDevice>) {
                    if (isActive()) onNearbyDevicesChanged(devices)
                }

                override fun onScanFailed(errorCode: Int) {
                    if (isActive()) onScanFailed(errorCode)
                }

                override fun onScanStarted() {
                    if (isActive()) onScanStarted?.invoke()
                }

                override fun onScanStopped() {
                    if (isActive()) onScanStopped?.invoke()
                }

                override fun onRequestPermissions(
                    permissions: Array<String>,
                    onResult: (Boolean) -> Unit,
                ) {
                    if (!isActive()) return
                    if (onRequestPermissions != null) {
                        onRequestPermissions(permissions, onResult)
                    } else {
                        onResult(false)
                    }
                }
            }
        }
    }
}
