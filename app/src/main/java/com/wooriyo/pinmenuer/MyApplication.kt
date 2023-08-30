package com.wooriyo.pinmenuer

import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import com.sewoo.jpos.printer.ESCPOSPrinter
import com.sewoo.port.android.BluetoothPort
import com.wooriyo.pinmenuer.db.AppDatabase
import com.wooriyo.pinmenuer.model.CategoryDTO
import com.wooriyo.pinmenuer.model.SharedDTO
import com.wooriyo.pinmenuer.model.StoreDTO
import java.util.*
import kotlin.collections.ArrayList

class MyApplication: Application() {
    companion object {
        lateinit var pref: SharedDTO
        lateinit var db : AppDatabase

        var density = 1.0F
        var dpi = 160

        var os = "A"
        var osver = 0
        lateinit var appver : String
        lateinit var md : String

        var useridx = 0
        var storeidx = 0
        lateinit var store: StoreDTO
        lateinit var androidId : String
        lateinit var allCateList : ArrayList<CategoryDTO>

        // 블루투스 관련 변수
        lateinit var bluetoothManager: BluetoothManager
        lateinit var bluetoothAdapter: BluetoothAdapter
        lateinit var remoteDevices: ArrayList<BluetoothDevice>
        lateinit var arrRemoteDevice : ArrayList<String>

        var bidx = 0    //프린터 설정 시 부여되는 idx (기기별 매장 하나 당 한개씩 부여)

        //세우전자 프린터 관련
        lateinit var bluetoothPort: BluetoothPort
        val escposPrinter = ESCPOSPrinter()
        val BT_PRINTER = 1536
        var btThread: Thread? = null

        // 영수증 프린터 연결 여부(추후 변경 가능성 높음)
//        var btConn = false

        fun setStoreDTO() {
            store = StoreDTO(useridx)
        }
    }

    override fun onCreate() {
        androidId = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)

        pref = SharedDTO(applicationContext)
        db = AppDatabase.getInstance(applicationContext)

        density = resources.displayMetrics.density
        dpi = resources.displayMetrics.densityDpi

        osver = Build.VERSION.SDK_INT
        appver = applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0).versionName
        md = Build.MODEL

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        var width = 0
        var height = 0

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            width = windowMetrics.bounds.width()
            height = windowMetrics.bounds.height()
        } else {
            val display = windowManager.defaultDisplay
            val realpoint = Point()
            display.getRealSize(realpoint) // or getSize(size)
            width = realpoint.x
            height = realpoint.y
        }

        if (width <= 1280) {
            density = 1.0F
            dpi = 160
        }

        setStoreDTO()
        allCateList = ArrayList<CategoryDTO>()

        //블루투스
        bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        remoteDevices = ArrayList<BluetoothDevice>()
        arrRemoteDevice = ArrayList<String>()

        //세우전자
        bluetoothPort = BluetoothPort.getInstance()
        bluetoothPort.SetMacFilter(false)

        super.onCreate()
    }
}