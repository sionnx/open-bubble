package io.bubble.core.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context

internal fun bluetoothAdapter(context: Context): BluetoothAdapter? {
    val manager = context.applicationContext.getSystemService(Context.BLUETOOTH_SERVICE)
        as? BluetoothManager
    return manager?.adapter
}
