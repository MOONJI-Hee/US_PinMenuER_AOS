package com.wooriyo.pinmenuer

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.wooriyo.pinmenuer.MyApplication.Companion.osver

class TestActivity : AppCompatActivity() {
    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter

    val REQUEST_ENABLE_BT = 1


    val TAG = "TestActivity"


    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (osver >= Build.VERSION_CODES.S) {
            permissions.plus(Manifest.permission.BLUETOOTH_CONNECT)
        }

        checkBluetooth()
    }

    fun checkBluetooth() {
        if(!bluetoothAdapter.isEnabled) {   // 블루투스 꺼져있음
            Toast.makeText(this@TestActivity, "블루투스 꺼졌음ㅠㅠ", Toast.LENGTH_SHORT).show()
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this@TestActivity, Manifest.permission.BLUETOOTH_CONNECT)) {
                    AlertDialog.Builder(this@TestActivity)
                        .setTitle(R.string.pms_bluetooth_title)
                        .setMessage(R.string.pms_bluetooth_content)
                        .setPositiveButton(R.string.confirm) { dialog, _ ->
                            dialog.dismiss()
                            getPms()
                        }
                        .setNegativeButton(R.string.cancel) {dialog, _ -> dialog.dismiss()}
                        .show()
                }
                getPms()
                return
            }
            turnOnBluetooth()
        }else {
            Toast.makeText(this@TestActivity, "블루투스 켜져있음..ㅋㅋ", Toast.LENGTH_SHORT).show()
        }
    }

    // 권한 확인하기
    fun checkPermissions() {
        for (pms in permissions) {
            if(ActivityCompat.checkSelfPermission(this@TestActivity, pms) != PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

    //블루투스 켜기
    fun turnOnBluetooth() {
        startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT)
    }

    //권한 받아오기
    fun getPms() {
        ActivityCompat.requestPermissions(this@TestActivity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_ENABLE_BT)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            REQUEST_ENABLE_BT -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    turnOnBluetooth()
                }
            }
        }
    }
}