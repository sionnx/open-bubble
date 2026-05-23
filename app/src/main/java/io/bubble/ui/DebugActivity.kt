package io.bubble.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.bubble.core.connect.BubbleConnectManager
import io.bubble.core.connect.ConnectListener
import io.bubble.core.connect.ConnectParams
import io.bubble.core.connect.ConnectState
import io.bubble.core.scan.BluetoothScanPermissions
import io.bubble.core.scan.DeviceScanController
import io.bubble.core.scan.ScannedDevice
import io.bubble.ui.component.BluetoothPermissionGate
import io.bubble.ui.screen.DebugActions
import io.bubble.ui.screen.DebugScanUiState
import io.bubble.ui.screen.DebugScreen
import io.bubble.ui.theme.BubbleTheme

class DebugActivity : ComponentActivity() {
    private var connectLabel by mutableStateOf("未连接")
    private var scanUiState by mutableStateOf(DebugScanUiState())
    private var scanController: DeviceScanController? = null
    private var permissionContinuation: ((Boolean) -> Unit)? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        val allGranted = grants.values.all { it }
        permissionContinuation?.invoke(allGranted)
        permissionContinuation = null
        if (!allGranted) {
            toast("需要蓝牙权限才能扫描/连接")
        }
    }

    private val connectListener = object : ConnectListener {
        override fun onStateChanged(mac: String, state: ConnectState, detail: String?) {
            connectLabel = when (state) {
                ConnectState.CONNECTING -> "连接中…"
                ConnectState.CONNECTED -> "已连接"
                ConnectState.DISCONNECTED -> "已断开"
                ConnectState.FAILED -> "连接失败"
                ConnectState.IDLE -> "未连接"
            }
            if (detail != null && state == ConnectState.FAILED) {
                toast(detail)
            }
        }

        override fun onConnected(mac: String, config: io.bubble.core.connect.TransferConfig) {
            toast("连接成功 mtu=${config.mtu}")
        }

        override fun onFailed(mac: String, code: Int, detail: String) {
            toast("连接失败($code): $detail")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BubbleConnectManager.init(applicationContext)
        BubbleConnectManager.addListener(connectListener)
        scanController = DeviceScanController(
            context = this,
            listener = DeviceScanController.forwardingListener(
                isActive = { !isFinishing && !isDestroyed },
                onBoundDevicesLoaded = { devices -> updateBoundUi(devices) },
                onNearbyDevicesChanged = { devices -> updateNearbyUi(devices) },
                onScanFailed = { code ->
                    scanUiState = scanUiState.copy(status = "扫描失败($code)")
                    toast("扫描失败($code)")
                },
                onRequestPermissions = { _, onResult ->
                    ensurePermissions { onResult(it) }
                },
                onScanStarted = {
                    scanUiState = scanUiState.copy(status = "扫描中…")
                },
                onScanStopped = {
                    scanUiState = scanUiState.copy(status = "扫描已停止")
                },
            ),
        )
        enableEdgeToEdge()
        setContent {
            BubbleTheme {
                BluetoothPermissionGate {
                    DebugScreen(
                        scanState = scanUiState,
                        actions = DebugActions(
                            onStartScan = { startDeviceScan() },
                            onStopScan = { scanController?.stopScan() },
                            onConnect = { ensurePermissionsAndConnect() },
                            onDisconnect = {
                                BubbleConnectManager.getActiveMac()?.let { mac ->
                                    BubbleConnectManager.disconnect(mac, "debug disconnect")
                                }
                            },
                        ),
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        scanController?.release()
        scanController = null
        BubbleConnectManager.removeListener(connectListener)
        super.onDestroy()
    }

    private fun startDeviceScan() {
        if (!BluetoothScanPermissions.isBluetoothEnabled(this)) {
            toast("请先开启蓝牙")
            return
        }
        val controller = scanController ?: return
        controller.loadBoundDevices()
        controller.requestScan()
    }

    private fun updateBoundUi(devices: List<ScannedDevice>) {
        scanUiState = scanUiState.copy(
            boundDevices = devices.map { "${it.displayName} ${it.mac}" },
        )
    }

    private fun updateNearbyUi(devices: List<ScannedDevice>) {
        scanUiState = scanUiState.copy(
            nearbyDevices = devices.map { "${it.displayName} ${it.mac}" },
            status = if (devices.isEmpty()) scanUiState.status else "已发现 ${devices.size} 台",
        )
    }

    private fun ensurePermissionsAndConnect() {
        ensurePermissions { granted ->
            if (granted) startPairConnect()
        }
    }

    private fun ensurePermissions(onResult: (Boolean) -> Unit) {
        if (BluetoothScanPermissions.allGranted(this)) {
            onResult(true)
            return
        }
        val missing = BluetoothScanPermissions.missingPermissions(this)
        permissionContinuation = onResult
        permissionLauncher.launch(missing)
    }

    private fun startPairConnect() {
        val firstNearby = scanController?.getNearbyDevices()?.firstOrNull()?.mac
        val mac = firstNearby ?: ConnectParams.DEFAULT_MAC
        connectLabel = "连接中…"
        BubbleConnectManager.connectDeviceByPair(
            ConnectParams(
                mac = mac,
                model = ConnectParams.DEFAULT_MODEL,
                reason = "connect",
            ),
        )
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
