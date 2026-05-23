package io.bubble.core.scan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import io.bubble.core.bluetooth.bluetoothAdapter

/**
 * 对齐 {@code x9.d}：蓝牙开关与运行时权限（无 OEM SDK）。
 */
object BluetoothScanPermissions {
    fun requiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        }
    }

    fun hasScanPermissions(context: Context): Boolean = allGranted(context)

    fun allGranted(context: Context): Boolean = missingPermissions(context).isEmpty()

    fun missingPermissions(context: Context): Array<String> {
        return requiredPermissions().filter { !hasPermission(context, it) }.toTypedArray()
    }

    fun isBluetoothEnabled(context: Context): Boolean {
        val adapter = bluetoothAdapter(context) ?: return false
        return adapter.isEnabled
    }

    private fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED
    }
}
