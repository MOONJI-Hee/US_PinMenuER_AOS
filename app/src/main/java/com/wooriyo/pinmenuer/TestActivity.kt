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
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sewoo.port.android.BluetoothPort
import com.sewoo.request.android.RequestHandler
import com.wooriyo.pinmenuer.MyApplication.Companion.osver
import com.wooriyo.pinmenuer.databinding.ActivityTestBinding
import com.wooriyo.pinmenuer.databinding.ListTestBinding
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

    val arrRemoteDevice = ArrayList<String>()
    val adapter = Adapter(arrRemoteDevice)

    var searchflags = false

    private val turnOnBluetoothResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == RESULT_OK) {
            getPairedDevice()
            searchDevice()
        }
    }




    // 브로드 리시버 시작
    val connectDevice =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
                Toast.makeText(getApplicationContext(), "BlueTooth Connect", Toast.LENGTH_SHORT).show();
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
                if (btThread != null) {
                    if(btThread!!.isAlive()) {
                        btThread!!.interrupt()
                        btThread = null
                    }
                }
//                ConnectionFailedDevice()

                Toast.makeText(getApplicationContext(), "BlueTooth Disconnect", Toast.LENGTH_SHORT).show();
            }
        }
    }

    val discoveryResult =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val key: String
            var bFlag = true
            var btDev: BluetoothDevice
            val remoteDevice =
                intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            if (remoteDevice != null) {
                val devNum = remoteDevice.bluetoothClass.majorDeviceClass
                if (devNum != BT_PRINTER) return
                key = if (remoteDevice.bondState != BluetoothDevice.BOND_BONDED) {
                    """${remoteDevice.name}[${remoteDevice.address}]""".trimIndent()
                } else {
                    """${remoteDevice.name}[${remoteDevice.address}] [Paired]""".trimIndent()
                }
                if (bluetoothPort.isValidAddress(remoteDevice.address)) {
                    for (i in remoteDevices.indices) {
                        btDev = remoteDevices.elementAt(i)
                        if (remoteDevice.address == btDev.address) {
                            bFlag = false
                            break
                        }
                    }
                    if (bFlag) {
                        remoteDevices.add(remoteDevice)
                        arrRemoteDevice.add(key)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }


    val searchStart =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //Toast.makeText(mainView, "블루투스 기기 검색 시작", Toast.LENGTH_SHORT).show();
        }
    }


    val searchFinish =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            searchflags = true
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

        bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        // BroadCast Receiver > 여기서 선언하는게 맞을지는 아직 모름
        registerReceiver(discoveryResult, IntentFilter(BluetoothDevice.ACTION_FOUND))
        registerReceiver(searchStart, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
        registerReceiver(searchFinish, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))


        remoteDevices = Vector<BluetoothDevice>()

        //세우전자 Lib
        bluetoothPort = BluetoothPort.getInstance()
        bluetoothPort.SetMacFilter(false) //not using mac address filtering

        // 권한 확인
        checkPermissions()

        binding.connect.setOnClickListener {
            connDevice()
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
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: InterruptedException) {
            // TODO Auto-generated catch block
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
        Log.d(TAG, "블루투스 기기 커넥트")
        Log.d(TAG, "remote 기기 > $remoteDevices")
        if(remoteDevices.isNotEmpty()) {
            Log.d(TAG, "remote 기기 있음")

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
            getPairedDevice()
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