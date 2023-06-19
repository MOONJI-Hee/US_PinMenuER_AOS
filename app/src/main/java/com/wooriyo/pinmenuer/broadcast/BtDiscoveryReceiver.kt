package com.wooriyo.pinmenuer.broadcast

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.MyApplication.Companion.arrRemoteDevice
import com.wooriyo.pinmenuer.MyApplication.Companion.remoteDevices
import com.wooriyo.pinmenuer.util.AppHelper

class BtDiscoveryReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return

//        val key: String
        var bFlag = true
        var btDev: BluetoothDevice
        val remoteDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        if (remoteDevice != null) {
            val devNum = remoteDevice.bluetoothClass.majorDeviceClass
            if (devNum != MyApplication.BT_PRINTER) return

//            key = if (remoteDevice.bondState != BluetoothDevice.BOND_BONDED) {
//                """${remoteDevice.name}[${remoteDevice.address}]""".trimIndent()
//            } else {
//                """${remoteDevice.name}[${remoteDevice.address}] [Paired]""".trimIndent()
//            }

            if (MyApplication.bluetoothPort.isValidAddress(remoteDevice.address)) {
                for (i in remoteDevices.indices) {
                    btDev = remoteDevices.elementAt(i)
                    if (remoteDevice.address == btDev.address) {
                        bFlag = false
                        break
                    }
                }
                if (bFlag) {
                    remoteDevices.add(remoteDevice)
//                    arrRemoteDevice.add(key)
                }
            }
            AppHelper.connDevice()
        }
    }
}