package com.wooriyo.pinmenuer

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sewoo.jpos.command.ESCPOSConst
import com.sewoo.jpos.printer.ESCPOSPrinter
import com.sewoo.request.android.RequestHandler
import com.wooriyo.pinmenuer.MyApplication.Companion.BT_PRINTER
import com.wooriyo.pinmenuer.MyApplication.Companion.arrRemoteDevice
import com.wooriyo.pinmenuer.MyApplication.Companion.bluetoothAdapter
import com.wooriyo.pinmenuer.MyApplication.Companion.bluetoothPort
import com.wooriyo.pinmenuer.MyApplication.Companion.btThread
import com.wooriyo.pinmenuer.MyApplication.Companion.osver
import com.wooriyo.pinmenuer.MyApplication.Companion.remoteDevices
import com.wooriyo.pinmenuer.broadcast.BtConnectReceiver
import com.wooriyo.pinmenuer.broadcast.BtDiscoveryReceiver
import com.wooriyo.pinmenuer.config.AppProperties.Companion.REQUEST_ENABLE_BT
import com.wooriyo.pinmenuer.config.AppProperties.Companion.REQUEST_LOCATION
import com.wooriyo.pinmenuer.databinding.ActivityTestBinding
import com.wooriyo.pinmenuer.databinding.ListTestBinding
import com.wooriyo.pinmenuer.util.AppHelper
import java.io.IOException
import java.util.*

class TestActivity : AppCompatActivity() {
    lateinit var binding: ActivityTestBinding

    val TAG = "TestActivity"

    val adapter = Adapter(arrRemoteDevice)

    var searchflags = false

    private val turnOnBluetoothResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == RESULT_OK) {
//            getPairedDevice()
            searchDevice()
        }
    }

    // 브로드 리시버 시작
    val connectDevice = BtConnectReceiver()
    val discoveryResult = BtDiscoveryReceiver()

    val searchStart =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //Toast.makeText(mainView, "블루투스 기기 검색 시작", Toast.LENGTH_SHORT).show();
        }
    }

    val searchFinish =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
//            searchflags = true
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

        binding.rv.layoutManager = LinearLayoutManager(this@TestActivity, LinearLayoutManager.VERTICAL, false)
        binding.rv.adapter = adapter

        // BroadCast Receiver > 여기서 선언하는게 맞을지는 아직 모름
        registerReceiver(discoveryResult, IntentFilter(BluetoothDevice.ACTION_FOUND))
        registerReceiver(searchStart, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
        registerReceiver(searchFinish, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))

        // 권한 확인
        checkPermissions()

        binding.connect.setOnClickListener {
            connDevice()
        }

        binding.print.setOnClickListener {
            print()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (bluetoothPort.isConnected) {
                bluetoothPort.disconnect()
                unregisterReceiver(connectDevice)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        if (btThread != null && btThread!!.isAlive) {
            btThread!!.interrupt()
            btThread = null
        }

        unregisterReceiver(searchFinish)
        unregisterReceiver(searchStart)
        unregisterReceiver(discoveryResult)
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
                grantResults.forEach {
                    if(it == PackageManager.PERMISSION_DENIED) {
                        getLocationPms()
                        return
                    }
                }

                if (osver >= Build.VERSION_CODES.S)
                    checkBluetoothPermission()
                else
                    checkBluetooth()
            }
        }
    }

    fun getPairedDevice() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        pairedDevices?.forEach { device ->
//            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address

            if(bluetoothPort.isValidAddress(deviceHardwareAddress)) {
                val deviceNum = device.bluetoothClass.majorDeviceClass

                if(deviceNum == BT_PRINTER) {
                    remoteDevices.add(device)
                }
            }
        }
    }

    fun searchDevice() {
        Log.d(TAG, "블루투스 기기 검색 시작~!~!~!~!~!")
        bluetoothAdapter.startDiscovery()
    }

    fun connDevice() {
        val retVal = AppHelper.connDevice()

        if (retVal == 0) { // Connection success.
            Log.d("AppHelper", "retVal 0이면 성공이야....")

            val rh = RequestHandler()
            btThread = Thread(rh)
            btThread!!.start()

//                saveSettingFile()
            registerReceiver(connectDevice, IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED))
            registerReceiver(connectDevice, IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED))
        } else { // Connection failed.
            Log.d("AppHelper", "retVal 0 아니면 실패야....")
//
//            AlertDialog.Builder()
//                .setTitle("Error")
//                .setMessage("Failed to connect Bluetooth device.")
//                .setNegativeButton(
//                    "CANCEL",
//                    DialogInterface.OnClickListener { dialog, which -> // TODO Auto-generated method stub
//                        dialog.dismiss()
//                    })
//                .show()
        }
    }

    // 권한 확인하기
    fun checkPermissions() {
        val deniedPms = ArrayList<String>()

        for (pms in permissions) {
            if(ActivityCompat.checkSelfPermission(this@TestActivity, pms) != PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this@TestActivity, pms)) {
                    AlertDialog.Builder(this@TestActivity)
                        .setTitle(R.string.pms_location_content)
                        .setMessage(R.string.pms_location_content)
                        .setPositiveButton(R.string.confirm) { dialog, _ ->
                            dialog.dismiss()
                            deniedPms.add(pms)
                        }
                        .setNegativeButton(R.string.cancel) {dialog, _ -> dialog.dismiss()}
                        .show()
                }else {
                    deniedPms.add(pms)
                }
            }
        }

        if(deniedPms.isEmpty()) {
            if (osver >= Build.VERSION_CODES.S)
                checkBluetoothPermission()
            else
                checkBluetooth()
        }else {
            getLocationPms()
        }
    }

    fun checkBluetoothPermission() {
        if(ActivityCompat.checkSelfPermission(this@TestActivity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            checkBluetooth()
        }else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this@TestActivity, Manifest.permission.BLUETOOTH_CONNECT)) {
                AlertDialog.Builder(this@TestActivity)
                    .setTitle(R.string.pms_bluetooth_title)
                    .setMessage(R.string.pms_bluetooth_content)
                    .setPositiveButton(R.string.confirm) { dialog, _ ->
                        dialog.dismiss()
                        getBluetoothPms()
                    }
                    .setNegativeButton(R.string.cancel) {dialog, _ -> dialog.dismiss()}
                    .show()
            }else getBluetoothPms()
        }
    }

    // 블루투스 ON/OFF 확인
    fun checkBluetooth() {
        if(!bluetoothAdapter.isEnabled) {   // 블루투스 꺼져있음
            turnOnBluetooth()
        }else {
            // TODO 블루투스 검색으로
//            getPairedDevice()
            searchDevice()
        }
    }

    //블루투스 ON
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

    fun print() {
        val escposPrinter = ESCPOSPrinter()
        var rtn = 0

        try {
            rtn = escposPrinter.printerSts()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            escposPrinter.printAndroidFont("나는 문지희다~! 하하하하", 512, 27, ESCPOSConst.LK_ALIGNMENT_LEFT)
            escposPrinter.lineFeed(2)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    class Adapter(val dataSet : ArrayList<String>): RecyclerView.Adapter<Adapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ListTestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(dataSet[position])
        }

        override fun getItemCount(): Int {
            return dataSet.size
        }

        class ViewHolder(val binding: ListTestBinding):RecyclerView.ViewHolder(binding.root) {
            fun bind(data: String) {
                binding.device.text = data
            }
        }
    }
}