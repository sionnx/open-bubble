package io.bubble.ui.component

import android.content.pm.PackageManager
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.bubble.core.scan.BluetoothScanPermissions

/**
 * 强制蓝牙权限门禁：未全部授予时展示不可关闭的 Bottom Sheet。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothPermissionGate(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    var allGranted by remember { mutableStateOf(BluetoothScanPermissions.allGranted(context)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        allGranted = grants.values.all { it } &&
            BluetoothScanPermissions.allGranted(context)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                allGranted = BluetoothScanPermissions.allGranted(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { target ->
            target != SheetValue.Hidden || allGranted
        },
    )

    LaunchedEffect(allGranted) {
        if (!allGranted) {
            sheetState.show()
        } else if (sheetState.isVisible) {
            sheetState.hide()
        }
    }

    Column(modifier = modifier) {
        content()
    }

    if (!allGranted) {
        ModalBottomSheet(
            onDismissRequest = { /* 未授权时不可关闭 */ },
            sheetState = sheetState,
            properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false),
        ) {
            PermissionSheetContent(
                permissionStates = buildPermissionStates(context),
                onRequestPermissions = {
                    val missing = BluetoothScanPermissions.missingPermissions(context)
                    if (missing.isNotEmpty()) {
                        permissionLauncher.launch(missing)
                    } else {
                        allGranted = true
                    }
                },
            )
        }
    }
}

@Composable
private fun PermissionSheetContent(
    permissionStates: List<PermissionRowState>,
    onRequestPermissions: () -> Unit,
) {
    val allGranted = permissionStates.all { it.granted }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "需要蓝牙权限",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "扫描与连接 OPPO 电子徽章需要以下权限。全部授予后本页将自动关闭。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        permissionStates.forEach { row ->
            PermissionRow(row)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onRequestPermissions,
            enabled = !allGranted,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (allGranted) "权限已就绪" else "授予权限")
        }
    }
}

@Composable
private fun PermissionRow(state: PermissionRowState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (state.granted) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (state.granted) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            },
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = state.label,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

private data class PermissionRowState(
    val permission: String,
    val label: String,
    val granted: Boolean,
)

private fun buildPermissionStates(context: android.content.Context): List<PermissionRowState> {
    return BluetoothScanPermissions.requiredPermissions().map { permission ->
        PermissionRowState(
            permission = permission,
            label = permissionLabel(permission),
            granted = ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
}

private fun permissionLabel(permission: String): String = when (permission) {
    android.Manifest.permission.BLUETOOTH_CONNECT -> "蓝牙连接"
    android.Manifest.permission.BLUETOOTH_SCAN -> "蓝牙扫描"
    android.Manifest.permission.BLUETOOTH -> "蓝牙"
    android.Manifest.permission.BLUETOOTH_ADMIN -> "蓝牙管理"
    android.Manifest.permission.ACCESS_FINE_LOCATION -> "精确位置（用于发现 BLE 设备）"
    else -> permission.substringAfterLast('.')
}
