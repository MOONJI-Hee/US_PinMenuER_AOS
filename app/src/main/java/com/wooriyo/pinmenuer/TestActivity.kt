package com.wooriyo.pinmenuer

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.sewoo.port.android.BluetoothPort
import com.sewoo.request.android.RequestHandler
import com.wooriyo.pinmenuer.MyApplication.Companion.osver
import com.wooriyo.pinmenuer.databinding.ActivityTestBinding
import java.io.IOException
import java.util.*

class TestActivity : AppCompatActivity() {
    lateinit var binding: ActivityTestBinding

    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var remoteDevices: Vector<BluetoothDevice>

    //세우전자 Lib
    lateinit var bluetoothPort: BluetoothPort
    private val BT_PRINTER = 1536
    private var btThread: Thread? = null


    val REQUEST_LOCATION = 0
    val REQUEST_ENABLE_BT = 1

    val TAG = "TestActivity"

    val turnOnBluetoothResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == RESULT_OK) {
            Log.d(TAG, "블루투스 켰다...!")
            getPairedDevice()
            searchDevice()
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceHardwareAddress = device?.address // MAC address

                    if(device != null) {
                        val deviceNum = device.bluetoothClass.majorDeviceClass

                        if(deviceNum != BT_PRINTER) return

                        if(bluetoothPort.isValidAddress(deviceHardwareAddress)) {
                            remoteDevices.forEach {
                                if(it.address == deviceHardwareAddress) {
//                                    bFlag = false;
                                    return;
                                }
                            }
//                            if(bFlag) {
//                                remoteDevices.add(remoteDevice);
//                                adapter.add(key);
//                            }
                        }
                    }

                }
            }
        }
    }

    private val connectDevice = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
                //Toast.makeText(getApplicationContext(), "BlueTooth Connect", Toast.LENGTH_SHORT).show();
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
                try {
                    if (bluetoothPort.isConnected()) bluetoothPort.disconnect()
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
                if (btThread != null && btThread!!.isAlive()) {
                    btThread!!.interrupt()
                    btThread = null
                }
//                ConnectionFailedDevice()

                //Toast.makeText(getApplicationContext(), "BlueTooth Disconnect", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        // BroadCast Receiver > 여기서 선언하는게 맞을지는 아직 모름
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        remoteDevices = Vector<BluetoothDevice>()

        //세우전자 Lib
        bluetoothPort = BluetoothPort.getInstance()
        bluetoothPort.SetMacFilter(false) //not using mac address filtering

        // 권한 확인
        if(!checkPermissions()) getLocationPms()

        if (osver >= Build.VERSION_CODES.S) {
            checkBluetoothPermission()
        }

        binding.connect.setOnClickListener {
            connDevice()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    fun getPairedDevice() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        pairedDevices?.forEach { device ->
//            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address

            if(bluetoothPort.isValidAddress(deviceHardwareAddress)) {
                val deviceNum = device.bluetoothClass.majorDeviceClass

                if(deviceNum == BT_PRINTER) {
                    remoteDevices.add(device);
                }
            }
        }
    }

    fun searchDevice() {
        bluetoothAdapter.startDiscovery()
    }

    fun connDevice() {
        if(remoteDevices.isNotEmpty()) {
            val connDvc = remoteDevices[0]

            var retVal: Int = 0
            var str_temp = ""

            try {
                bluetoothPort.connect(connDvc)
                str_temp = connDvc.address
                retVal = Integer.valueOf(0)
            } catch (e: IOException) {
                e.printStackTrace()
                retVal = Integer.valueOf(-1)
            }

            if (retVal == 0) // Connection success.
            {
                val rh = RequestHandler()
                btThread = Thread(rh)
                btThread!!.start()

//                saveSettingFile()
                registerReceiver(connectDevice, IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED))
                registerReceiver(connectDevice, IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED))
            } else  // Connection failed.
            {
                AlertDialog.Builder(this@TestActivity)
                    .setTitle("Error")
                    .setMessage("Failed to connect Bluetooth device.")
                    .setNegativeButton(
                        "CANCEL",
                        DialogInterface.OnClickListener { dialog, which -> // TODO Auto-generated method stub
                            dialog.dismiss()
                        })
                    .show()
            }
        }
    }

    // 권한 확인하기
    fun checkPermissions(): Boolean {
        for (pms in permissions) {
            if(ActivityCompat.checkSelfPermission(this@TestActivity, pms) != PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this@TestActivity, pms)) {
                    AlertDialog.Builder(this@TestActivity)
                        .setTitle(R.string.pms_bluetooth_title)
                        .setMessage(R.string.pms_bluetooth_content)
                        .setPositiveButton(R.string.confirm) { dialog, _ ->
                            dialog.dismiss()
                            getBluetoothPms()
                        }
                        .setNegativeButton(R.string.cancel) {dialog, _ -> dialog.dismiss()}
                        .show()
                }
                return false
            }
        }
        return true
    }

    fun checkBluetoothPermission() {
        when {
            ActivityCompat.checkSelfPermission(this@TestActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED -> getBluetoothPms()
            ActivityCompat.shouldShowRequestPermissionRationale(this@TestActivity, Manifest.permission.BLUETOOTH_CONNECT) -> {
                AlertDialog.Builder(this@TestActivity)
                    .setTitle(R.string.pms_bluetooth_title)
                    .setMessage(R.string.pms_bluetooth_content)
                    .setPositiveButton(R.string.confirm) { dialog, _ ->
                        dialog.dismiss()
                        getBluetoothPms()
                    }
                    .setNegativeButton(R.string.cancel) {dialog, _ -> dialog.dismiss()}
                    .show()
            }
            else -> checkBluetooth()
        }
    }

    // 블루투스 on/off 확인
    fun checkBluetooth() {
        if(!bluetoothAdapter.isEnabled) {   // 블루투스 꺼져있음
            turnOnBluetooth()
        }else {
            // TODO 블루투스 검색으로
        }
    }

    //블루투스 켜기
    private fun turnOnBluetooth() {
        turnOnBluetoothResult.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
    }

    //권한 받아오기
    fun getLocationPms() {
        ActivityCompat.requestPermissions(this@TestActivity, permissions, REQUEST_LOCATION)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun getBluetoothPms() {
        ActivityCompat.requestPermissions(this@TestActivity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_ENABLE_BT)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            REQUEST_ENABLE_BT -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkBluetooth()
                }
            }
            REQUEST_LOCATION -> {
                checkBluetooth()
            }
        }
    }
}