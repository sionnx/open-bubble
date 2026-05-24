package io.bubble.ui.component

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.bubble.core.connect.BubbleConnectManager
import io.bubble.core.connect.ConnectListener
import io.bubble.core.connect.ConnectParams
import io.bubble.core.connect.ConnectState
import io.bubble.core.connect.TransferConfig
import io.bubble.core.scan.BluetoothScanPermissions
import io.bubble.core.scan.DeviceScanController
import io.bubble.core.scan.ScannedDevice
import io.bubble.preferences.AppPreferencesRepository
import kotlinx.coroutines.launch

data class DeviceScanSheetState(
    val status: String = "准备扫描",
    val isScanning: Boolean = false,
    val boundDevices: List<ScannedDevice> = emptyList(),
    val nearbyDevices: List<ScannedDevice> = emptyList(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScanBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
) {
    if (!visible) return

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val isActive = remember(lifecycleOwner) {
        { lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) }
    }

    var scanState by remember { mutableStateOf(DeviceScanSheetState()) }
    val scanStateRef = rememberUpdatedState(scanState)
    var deviceConnectStates by remember { mutableStateOf<Map<String, ConnectState>>(emptyMap()) }
    var connectErrors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var permissionContinuation by remember { mutableStateOf<((Boolean) -> Unit)?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        val allGranted = grants.values.all { it }
        permissionContinuation?.invoke(allGranted)
        permissionContinuation = null
    }

    val preferencesRepository = remember { AppPreferencesRepository.getInstance(context) }

    DisposableEffect(Unit) {
        val listener = object : ConnectListener {
            override fun onStateChanged(mac: String, state: ConnectState, detail: String?) {
                scope.launch {
                    deviceConnectStates = deviceConnectStates + (mac to state)
                    if (state == ConnectState.FAILED && detail != null) {
                        connectErrors = connectErrors + (mac to detail)
                    }
                }
            }

            override fun onConnected(mac: String, config: TransferConfig) {
                scope.launch {
                    deviceConnectStates = deviceConnectStates + (mac to ConnectState.CONNECTED)
                    connectErrors = connectErrors - mac
                    val currentScanState = scanStateRef.value
                    val displayName = currentScanState.nearbyDevices.find { it.mac == mac }?.displayName
                        ?: currentScanState.boundDevices.find { it.mac == mac }?.displayName
                    preferencesRepository.setLastConnected(mac, displayName)
                }
            }

            override fun onFailed(mac: String, code: Int, detail: String) {
                scope.launch {
                    deviceConnectStates = deviceConnectStates + (mac to ConnectState.FAILED)
                    connectErrors = connectErrors + (mac to detail)
                }
            }
        }
        BubbleConnectManager.addListener(listener)
        onDispose { BubbleConnectManager.removeListener(listener) }
    }

    val scanController = remember {
        DeviceScanController(
            context = context,
            listener = DeviceScanController.forwardingListener(
                isActive = isActive,
                onBoundDevicesLoaded = { devices ->
                    scanState = scanState.copy(boundDevices = devices)
                },
                onNearbyDevicesChanged = { devices ->
                    scanState = scanState.copy(
                        nearbyDevices = devices,
                        status = if (devices.isEmpty()) {
                            scanState.status
                        } else {
                            "已发现 ${devices.size} 台设备"
                        },
                    )
                },
                onScanFailed = { code ->
                    scanState = scanState.copy(
                        isScanning = false,
                        status = "扫描失败 ($code)",
                    )
                },
                onRequestPermissions = { _, onResult ->
                    if (BluetoothScanPermissions.allGranted(context)) {
                        onResult(true)
                    } else {
                        permissionContinuation = onResult
                        permissionLauncher.launch(
                            BluetoothScanPermissions.missingPermissions(context),
                        )
                    }
                },
                onScanStarted = {
                    scanState = scanState.copy(
                        isScanning = true,
                        status = "正在扫描…",
                        nearbyDevices = emptyList(),
                    )
                },
                onScanStopped = {
                    scanState = scanState.copy(
                        isScanning = false,
                        status = if (scanState.nearbyDevices.isEmpty()) {
                            "扫描已结束，未发现设备"
                        } else {
                            "扫描已结束"
                        },
                    )
                },
            ),
        )
    }

    DisposableEffect(Unit) {
        onDispose { scanController.release() }
    }

    fun connectDevice(device: ScannedDevice) {
        deviceConnectStates = deviceConnectStates + (device.mac to ConnectState.CONNECTING)
        connectErrors = connectErrors - device.mac
        BubbleConnectManager.connectDeviceByPair(
            ConnectParams(mac = device.mac, reason = "connect"),
        )
    }

    fun disconnectDevice(mac: String) {
        BubbleConnectManager.disconnect(mac, "user disconnect")
    }

    fun startScan() {
        if (!BluetoothScanPermissions.isBluetoothEnabled(context)) {
            scanState = scanState.copy(
                isScanning = false,
                status = "请先开启蓝牙",
            )
            return
        }
        scanController.loadBoundDevices()
        scanController.requestScan()
    }

    LaunchedEffect(Unit) {
        val activeMac = BubbleConnectManager.getActiveMac()
        val state = BubbleConnectManager.getState()
        if (activeMac != null && state == ConnectState.CONNECTED) {
            deviceConnectStates = deviceConnectStates + (activeMac to ConnectState.CONNECTED)
        }
        startScan()
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = {
            scanController.stopScan()
            onDismiss()
        },
        sheetState = sheetState,
    ) {
        DeviceScanSheetContent(
            scanState = scanState,
            deviceConnectStates = deviceConnectStates,
            connectErrors = connectErrors,
            onConnect = ::connectDevice,
            onDisconnect = ::disconnectDevice,
            onRescan = { startScan() },
            onStop = { scanController.stopScan() },
        )
    }
}

@Composable
private fun DeviceScanSheetContent(
    scanState: DeviceScanSheetState,
    deviceConnectStates: Map<String, ConnectState>,
    connectErrors: Map<String, String>,
    onConnect: (ScannedDevice) -> Unit,
    onDisconnect: (String) -> Unit,
    onRescan: () -> Unit,
    onStop: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "扫描设备",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (scanState.isScanning) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            }
            Text(
                text = scanState.status,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (scanState.boundDevices.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "已绑定",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
            )
            scanState.boundDevices.forEach { device ->
                ScannedDeviceRow(
                    device = device,
                    connectState = deviceConnectStates[device.mac],
                    connectError = connectErrors[device.mac],
                    onConnect = { onConnect(device) },
                    onDisconnect = { onDisconnect(device.mac) },
                )
            }
            HorizontalDivider()
        }

        Text(
            text = "附近设备",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
        )

        if (scanState.nearbyDevices.isEmpty()) {
            Text(
                text = if (scanState.isScanning) "搜索中，请靠近设备…" else "暂无附近设备",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            LazyColumn(
                modifier = Modifier.height(240.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(scanState.nearbyDevices, key = { it.mac }) { device ->
                    ScannedDeviceRow(
                        device = device,
                        connectState = deviceConnectStates[device.mac],
                        connectError = connectErrors[device.mac],
                        onConnect = { onConnect(device) },
                        onDisconnect = { onDisconnect(device.mac) },
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onStop,
                enabled = scanState.isScanning,
                modifier = Modifier.weight(1f),
            ) {
                Text("停止")
            }
            OutlinedButton(
                onClick = onRescan,
                enabled = !scanState.isScanning,
                modifier = Modifier.weight(1f),
            ) {
                Text("重新扫描")
            }
        }
    }
}

@Composable
private fun ScannedDeviceRow(
    device: ScannedDevice,
    connectState: ConnectState?,
    connectError: String?,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = device.displayName,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = device.mac,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (connectError != null) {
                Text(
                    text = connectError,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        when (connectState) {
            ConnectState.CONNECTING -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                    Text(
                        text = "连接中",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            ConnectState.CONNECTED -> {
                OutlinedButton(onClick = onDisconnect) {
                    Text("断开")
                }
            }
            else -> {
                Button(onClick = onConnect) {
                    Text("连接")
                }
            }
        }
    }
}
