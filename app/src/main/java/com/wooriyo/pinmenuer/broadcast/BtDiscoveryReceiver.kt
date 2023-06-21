package com.wooriyo.pinmenuer.broadcast

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.sewoo.request.android.RequestHandler
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.MyApplication.Companion.arrRemoteDevice
import com.wooriyo.pinmenuer.MyApplication.Companion.remoteDevices
import com.wooriyo.pinmenuer.util.AppHelper

class BtDiscoveryReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return

        var bFlag = true
        var btDev: BluetoothDevice
        val remoteDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        if (remoteDevice != null) {
            val devNum = remoteDevice.bluetoothClass.majorDeviceClass
            if (devNum != MyApplication.BT_PRINTER) return

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
                }
            }
            val retVal = AppHelper.connDevice()

            if (retVal == 0) { // Connection success.
                val rh = RequestHandler()
                MyApplication.btThread = Thread(rh)
                MyApplication.btThread!!.start()
            } else // Connection failed.
                Toast.makeText(context, "블루투스 연결 실패", Toast.LENGTH_SHORT).show()
        }else
            Toast.makeText(context, "검색된 블루투스 기기가 없습니다.", Toast.LENGTH_SHORT).show()
    }
}