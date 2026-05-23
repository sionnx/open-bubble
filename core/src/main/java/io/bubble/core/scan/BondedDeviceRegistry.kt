package io.bubble.core.scan

import android.annotation.SuppressLint
import android.content.Context
import io.bubble.core.bluetooth.bluetoothAdapter
import java.util.Locale

/**
 * 无 HeyTap DeviceManager 时，用系统已配对设备作为「已绑定」列表来源。
 */
object BondedDeviceRegistry {
    @SuppressLint("MissingPermission")
    fun loadBondedMacs(context: Context): List<String> {
        if (!BluetoothScanPermissions.hasScanPermissions(context)) {
            return emptyList()
        }
        val adapter = bluetoothAdapter(context) ?: return emptyList()
        return adapter.bondedDevices
            ?.map { it.address.uppercase(Locale.ROOT) }
            ?.distinct()
            ?: emptyList()
    }

}
