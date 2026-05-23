package io.bubble.core.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** 系统蓝牙配对，对应 LinkService createBond 前置步骤。 */
object BondHelper {
    suspend fun ensureBonded(context: Context, device: BluetoothDevice) {
        if (device.bondState == BluetoothDevice.BOND_BONDED) return
        if (device.bondState == BluetoothDevice.BOND_BONDING) {
            awaitBond(context, device)
            return
        }
        val started = device.createBond()
        if (!started) {
            throw IllegalStateException("createBond returned false for ${device.address}")
        }
        awaitBond(context, device)
    }

    private suspend fun awaitBond(context: Context, device: BluetoothDevice) {
        suspendCancellableCoroutine { cont ->
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context?, intent: Intent?) {
                    if (intent?.action != BluetoothDevice.ACTION_BOND_STATE_CHANGED) return
                    val dev = if (Build.VERSION.SDK_INT >= 33) {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    } ?: return
                    if (!dev.address.equals(device.address, ignoreCase = true)) return
                    when (dev.bondState) {
                        BluetoothDevice.BOND_BONDED -> {
                            ctx?.unregisterReceiver(this)
                            cont.resume(Unit)
                        }
                        BluetoothDevice.BOND_NONE -> {
                            ctx?.unregisterReceiver(this)
                            cont.resumeWithException(
                                IllegalStateException("bond failed for ${device.address}"),
                            )
                        }
                    }
                }
            }
            val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            if (Build.VERSION.SDK_INT >= 33) {
                context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                context.registerReceiver(receiver, filter)
            }
            cont.invokeOnCancellation {
                runCatching { context.unregisterReceiver(receiver) }
            }
        }
    }

    fun remoteDevice(mac: String): BluetoothDevice {
        val adapter = BluetoothAdapter.getDefaultAdapter()
            ?: throw IllegalStateException("Bluetooth adapter unavailable")
        if (!BluetoothAdapter.checkBluetoothAddress(mac)) {
            throw IllegalArgumentException("invalid mac: $mac")
        }
        return adapter.getRemoteDevice(mac.uppercase())
    }
}
