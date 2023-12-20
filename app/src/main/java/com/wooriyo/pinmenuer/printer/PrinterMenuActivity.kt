package com.wooriyo.pinmenuer.printer

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.MyApplication.Companion.androidId
import com.wooriyo.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.config.AppProperties
import com.wooriyo.pinmenuer.databinding.ActivityPrinterMenuBinding
import com.wooriyo.pinmenuer.model.PrintDTO
import com.wooriyo.pinmenuer.model.PrintListDTO
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrinterMenuActivity : BaseActivity() {
    lateinit var binding: ActivityPrinterMenuBinding

    val printerList = ArrayList<PrintDTO>()

    private val turnOnBluetoothResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == RESULT_OK) { }
    }

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val permissions_bt = arrayOf(
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrinterMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 권한 확인
        checkPermissions()

        binding.back.setOnClickListener{finish()}
        binding.setConn.setOnClickListener {
            if(printerList.isEmpty()) {
                startActivity(Intent(mActivity, NewConnActivity::class.java))
            }else {
                val intent = Intent(mActivity, SetConnActivity::class.java)
                intent.putExtra("printerList", printerList)
                startActivity(intent)
            }
        }
        binding.model.setOnClickListener { startActivity(Intent(mActivity, PrinterModelListActivity::class.java)) }
        binding.setContents.setOnClickListener { startActivity(Intent(mActivity, SetContentActivity::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        getConnPrintList()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            AppProperties.REQUEST_ENABLE_BT -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkBluetooth()
                }
            }
            AppProperties.REQUEST_LOCATION -> {
                grantResults.forEach {
                    if(it == PackageManager.PERMISSION_DENIED) {
                        getLocationPms()
                        return
                    }
                }
                if (MyApplication.osver >= Build.VERSION_CODES.S)
                    checkBluetoothPermission()
                else
                    checkBluetooth()
            }
        }
    }

    // 권한 확인하기
    fun checkPermissions() {
        val deniedPms = java.util.ArrayList<String>()

        for (pms in permissions) {
            if(ActivityCompat.checkSelfPermission(mActivity, pms) != PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity, pms)) {
                    AlertDialog.Builder(mActivity)
                        .setTitle(R.string.pms_location_content)
                        .setMessage(R.string.pms_location_content)
                        .setPositiveButton(R.string.confirm) { dialog, _ ->
                            dialog.dismiss()
                            getLocationPms()
                        }
                        .setNegativeButton(R.string.cancel) {dialog, _ -> dialog.dismiss()}
                        .show()
                }else {
                    deniedPms.add(pms)
                }
            }
        }

        if(deniedPms.isEmpty()) {
            if (MyApplication.osver >= Build.VERSION_CODES.S)
                checkBluetoothPermission()
            else
                checkBluetooth()
        }else {
            getLocationPms()
        }
    }

    fun checkBluetoothPermission() {
        val deniedPms = ArrayList<String>()

        for (pms in permissions_bt) {
            if(ActivityCompat.checkSelfPermission(mActivity, pms) != PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity, pms)) {
                    AlertDialog.Builder(mActivity)
                        .setTitle(R.string.pms_bluetooth_title)
                        .setMessage(R.string.pms_bluetooth_content)
                        .setPositiveButton(R.string.confirm) { dialog, _ ->
                            dialog.dismiss()
                            getBluetoothPms()
                            return@setPositiveButton
                        }
                        .setNegativeButton(R.string.cancel) {dialog, _ ->
                            dialog.dismiss()
                            return@setNegativeButton
                        }
                        .show()
                }else
                    deniedPms.add(pms)
            }
        }

        if(deniedPms.isEmpty() || deniedPms.size == 0) {
            checkBluetooth()
        }else {
            getBluetoothPms()
        }
    }

    // 블루투스 ON/OFF 확인
    fun checkBluetooth() {
        if(!MyApplication.bluetoothAdapter.isEnabled) {   // 블루투스 꺼져있음
            turnOnBluetooth()
        }else {} // 켜져있음
    }

    //블루투스 ON
    private fun turnOnBluetooth() {
        turnOnBluetoothResult.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
    }

    //권한 받아오기
    fun getLocationPms() {
        ActivityCompat.requestPermissions(mActivity, permissions,
            AppProperties.REQUEST_LOCATION
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun getBluetoothPms() {
        ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
            AppProperties.REQUEST_ENABLE_BT
        )
    }

    fun getConnPrintList() {
        ApiClient.service.connPrintList(useridx, storeidx, androidId).enqueue(object : Callback<PrintListDTO> {
            override fun onResponse(call: Call<PrintListDTO>, response: Response<PrintListDTO>) {
                Log.d(TAG, "등록된 프린터 리스트 조회 URL : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return

                when(result.status) {
                    1 -> {
                        printerList.clear()
                        printerList.addAll(result.myprintList)
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PrintListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "등록된 프린터 리스트 조회 오류 >> $t")
                Log.d(TAG, "등록된 프린터 리스트 조회 오류 >> ${call.request()}")
            }
        })
    }
}