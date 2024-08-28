package com.wooriyo.us.pinmenuer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.graphics.Point
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.rt.printerlibrary.factory.printer.ThermalPrinterFactory
import com.rt.printerlibrary.printer.RTPrinter
import com.rt.printerlibrary.printer.ThermalPrinter
import com.sewoo.jpos.printer.ESCPOSPrinter
import com.sewoo.port.android.BluetoothPort
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.config.AppProperties
import com.wooriyo.us.pinmenuer.db.AppDatabase
import com.wooriyo.us.pinmenuer.model.CategoryDTO
import com.wooriyo.us.pinmenuer.model.SharedDTO
import com.wooriyo.us.pinmenuer.model.StoreDTO
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
        var engStoreName = ""

        lateinit var arrRemoteDevice : ArrayList<String>
        // 블루투스 관련 객체 (영수증 프린터 연결)
        lateinit var bluetoothManager: BluetoothManager
        lateinit var bluetoothAdapter: BluetoothAdapter
        lateinit var pairedDevices: ArrayList<BluetoothDevice>  // 페어링 기기 목록
        lateinit var remoteDevices: ArrayList<BluetoothDevice>  // 연결 가능한 기기 목록 (페어링 + 검색 기기)
        var btThread: Thread? = null                            // 블루투스 기기 검색 / 연결 시 사용 쓰레드

        var bidx = 0    //프린터 설정 시 부여되는 idx (기기별 매장 하나 당 한개씩 부여)

        // RP325
        lateinit var rtPrinter: RTPrinter<ThermalPrinter>

        //세우전자 프린터 관련
        lateinit var bluetoothPort: BluetoothPort
        lateinit var escposPrinter: ESCPOSPrinter
        var connDev_sewoo = "00:00:00:00:00:00"

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
        pairedDevices = ArrayList()
        remoteDevices = ArrayList()

        // RP325 프린터
        val printerFactory = ThermalPrinterFactory()
        rtPrinter = printerFactory.create() as RTPrinter<ThermalPrinter>

        //세우전자 프린터
        bluetoothPort = BluetoothPort.getInstance()
        bluetoothPort.SetMacFilter(false)
        escposPrinter = ESCPOSPrinter()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.deleteNotificationChannel(AppProperties.CHANNEL_ID_ORDER)
        notificationManager.deleteNotificationChannel(AppProperties.CHANNEL_ID_CALL)

        val ordSound = R.raw.customnoti
        val ordUri: Uri = Uri.parse("android.resource://com.wooriyo.us.pinmenuer/$ordSound")

        val callSound = R.raw.customcall
        val callUri: Uri = Uri.parse("android.resource://com.wooriyo.us.pinmenuer/$callSound")

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        // 주문 알림 채널 생성
        val ordChannel = NotificationChannel(AppProperties.CHANNEL_ID_ORDER, "새 주문 알림", NotificationManager.IMPORTANCE_HIGH)
        ordChannel.enableLights(true)
        ordChannel.enableVibration(true)
        ordChannel.setSound(ordUri, audioAttributes)
        ordChannel.lockscreenVisibility = 1
        notificationManager.createNotificationChannel(ordChannel)

        // 호출 알림 채널 생성
        val callChannel = NotificationChannel(AppProperties.CHANNEL_ID_CALL, "새 호출 알림", NotificationManager.IMPORTANCE_HIGH)
        callChannel.enableLights(true)
        callChannel.enableVibration(true)
        callChannel.setSound(callUri, audioAttributes)
        ordChannel.lockscreenVisibility = 1
        notificationManager.createNotificationChannel(callChannel)

    }
}