package com.wooriyo.us.pinmenuer.util

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import com.wooriyo.us.pinmenuer.MyApplication
import com.wooriyo.us.pinmenuer.MyApplication.Companion.bluetoothAdapter
import com.wooriyo.us.pinmenuer.MyApplication.Companion.remoteDevices
import com.wooriyo.us.pinmenuer.config.AppProperties
import com.wooriyo.us.pinmenuer.model.OrderDTO
import java.io.IOException
import java.lang.reflect.Method
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern

// 자주 쓰는 메소드 모음 - 문지희 (2022.10 갱신)
class AppHelper {
    companion object {
        private val emailReg = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$".toRegex()
        private val pwReg = "^(?=.*[a-zA-Z0-9]).{8,}$".toRegex()

        // 네비게이션바 숨기기
        fun hideInset(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity.window.insetsController?.hide(android.view.WindowInsets.Type.navigationBars())
            }else {
                activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            }
        }

        // 바깥 클릭했을 때 키보드 내리기
        fun hideKeyboard(context: Context, focusView: View?, ev: MotionEvent) {
            if (focusView != null) {
                val rect = Rect()
                focusView.getGlobalVisibleRect(rect)
                val x = ev.x.toInt()
                val y = ev.y.toInt()
                if (!rect.contains(x, y)) {
                    val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                    focusView.clearFocus()
                }
            }
        }

        fun verifyEmail(email: String):Boolean {
            return email.matches(emailReg)
        }

        fun verifyPw(pw: String): Boolean {
            return pw.matches(pwReg)
        }

        val dec = DecimalFormat("00")
        val nformat = NumberFormat.getInstance()

        // 천 단위 콤마 찍기
        fun price(n: Int): String {
            return nformat.format(n)
        }

        // 한자리 수 n > 0n 형식으로 변환하기 + 빈 문자열 > 00으로 변환
        fun mkDouble(n: String): String {
            return if(n == "") {
                "00"
            } else {
                dec.format(n.toInt())
            }
        }

        // 정수 > 00 형태의 문자열로 리턴
        fun intToString(n: Int): String {
            return if(n in 1..9) {"0$n"} else n.toString()
        }

        // 현재 일시 yyyy-mm-dd 형식으로 리턴
        fun getToday(): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate.now().toString()
            }else {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
                dateFormat.format(Date())
            }
        }

        // 오늘 날짜와 비교 - true: 오늘, false : 오늘 아님
        fun CompareToday(strDate: String?): Boolean {
            return if (strDate.isNullOrEmpty()) { false }
                else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val list    = strDate.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                        val year    = list[0].toInt()
                        val month   = list[1].toInt()
                        val day     = list[2].toInt()

                        val today = LocalDate.now()
                        val date = LocalDate.of(year, month, day)
                        today.isEqual(date)
                    } else {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

                        val now = Calendar.getInstance()
                        val today: String = dateFormat.format(now.time)
                        today == strDate
                    }
                }
        }

        // 현재 날짜와 비교
        fun dateNowCompare(dt: String?): Boolean {    // 과거 : false, 현재 혹은 미래 : true
            return if(dt.isNullOrEmpty()) {
                false
            }else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val strDt = dt.replace(" ", "T")

                    val now = LocalDateTime.now()
                    val day = LocalDateTime.parse(strDt)

                    val cmp = day.compareTo(now)

                    cmp >= 0
                }else {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

                    val now = System.currentTimeMillis()
                    val day: Long = dateFormat.parse(dt)?.time ?: 0L

                    day - now >= 0
                }
            }
        }

        // 버전 비교
        fun compareVer(curver: String): Boolean {
            val arr_cur = curver.split(".")
            val arr_app = MyApplication.appver.split(".")

            if(curver == MyApplication.appver) return true

            for(i : Int in arr_app.indices) {
                if(arr_app[i].toInt() > arr_cur[i].toInt()) {
                    return true
                }
            }
            return false
        }

        fun setHeight(v: View, height: Int) {
            val hpx = (height * MyApplication.density).toInt()

            val params = v.layoutParams
            params.height = hpx
            v.layoutParams = params
        }

        // APPCALL 거래번호 생성
        fun getAppCallNo(): String {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appCallFormatter = DateTimeFormatter.ofPattern("yyMMddHHmmss")
                return appCallFormatter.format(LocalDateTime.now())
            }else {
                val appCallFormatter = SimpleDateFormat("yyMMddHHmmss")
                return appCallFormatter.format(Date())
            }
        }

        fun osVersion(): Int = Build.VERSION.SDK_INT    // 안드로이드 버전
        fun versionName(context: Context): String = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        fun getPhoneModel(): String = Build.MODEL       // 디바이스 모델명

        // 블루투스 & 프린터기 연결 관련 메소드
        fun searchDevice() {
            Log.d("AppHelper", "블루투스 기기 찾기")
            MyApplication.bluetoothAdapter.startDiscovery()
        }

        fun connDevice(): Int {
            var retVal: Int = -1

            Log.d("AppHelper", "블루투스 기기 커넥트")
            Log.d("AppHelper", "remote 기기 > $remoteDevices")
            if(remoteDevices.isNotEmpty()) {
                val connDvc = remoteDevices[0]
                Log.d("AppHelper", "connDvc >> $connDvc")

                try {
                    MyApplication.bluetoothPort.connect(connDvc)
                    retVal = Integer.valueOf(0)
                } catch (e: IOException) {
                    e.printStackTrace()
                    retVal = Integer.valueOf(-1)
                }
            }else {
                retVal = -2
            }
            return retVal
        }

        fun getPairedDevice() {
            remoteDevices.clear()

            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
            pairedDevices?.forEach { device ->
//            val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC address

                if(MyApplication.bluetoothPort.isValidAddress(deviceHardwareAddress)) {
                    val deviceNum = device.bluetoothClass.majorDeviceClass

                    if(deviceNum == MyApplication.BT_PRINTER) {
                        remoteDevices.add(device)
                    }
                }
            }
            Log.d("AppHelper", "페어링된 기기 목록 >>$remoteDevices")
            checkPrinterConn()
        }

        fun checkPrinterConn() {
            Log.d("AppHelper", "CheckPrintConnn~~!~!~!")
            remoteDevices.forEach {
                val m: Method = it.javaClass.getMethod("isConnected")
                val connected = m.invoke(it) as Boolean

                if(connected) {
                    val deviceHardwareAddress = it.address // MAC address

                    if(MyApplication.bluetoothPort.isValidAddress(deviceHardwareAddress)) {
                        val deviceNum = it.bluetoothClass.majorDeviceClass
                        Log.d("AppHelper", "연결된 기기 == 세우테크 프린터 맞음")

                    }
                }
            }
        }

        fun getPrint(ord: OrderDTO) : String {
            var one_line = AppProperties.ONE_LINE_BIG
            var space = AppProperties.SPACE

            var total = 0.0
            var name = 0.0

            val result: StringBuilder = StringBuilder()
            val underline1 = StringBuilder()
            val underline2 = StringBuilder()

            ord.name.forEach {
                val charSize = getSewooCharSize(it)
//                Log.d("AppHelper", "charSize $it >> $charSize")

                if (total + 1 < one_line){
                    result.append(it)
                    name += charSize
                }
                else if (total + 1 < (one_line * 2))
                    underline1.append(it)
                else
                    underline2.append(it)

                if (it == ' ') {
                    total++
                } else
                    total += charSize
            }

            var diff = (one_line - name + 1.5).toInt()

//            Log.d("AppHelper", "total >> $total")
//            Log.d("AppHelper", "name >> $name")
//            Log.d("AppHelper", "diff >> $diff")

            if (MyApplication.store.fontsize == 1) {
                space = if(ord.price >= 100000) 1
                else if(ord.price >= 10000) 2
                else if (ord.price >= 1000) 3
                else if (ord.price >= 100) 6
                else if (ord.price >= 10) 7
                else if (ord.price >= 0) 8
                else 1

                if(ord.gea < 10) {
                    space++
                }

            } else if (MyApplication.store.fontsize == 2) {
                if (ord.gea < 10) {
                    diff += 1
                    space += 2
                } else if (ord.gea < 100) {
                    space += 1
                }
            }

            for (i in 1..diff) {
                result.append(" ")
            }

            result.append(ord.gea.toString())

            for (i in 1..space) {
                result.append(" ")
            }

//            var togo = ""
//            when (ord.togotype) {
//                1 -> togo = "신규"
//                2 -> togo = "포장"
//            }
//            result.append(togo)

            result.append(price(ord.price))

            if (underline1.toString() != "")
                result.append("\n$underline1")

            if (underline2.toString() != "")
                result.append("\n$underline2")

            if(!ord.opt.isNullOrEmpty()) {
                ord.opt.forEach {
                    result.append("\n - $it")
                }
            }

            return result.toString()
        }

        fun getSewooCharSize(c: Char): Double {
            if(Pattern.matches("^[ㄱ-ㅎ가-힣]*\$", c.toString())) {
                return AppProperties.HANGUL_SIZE
            }
            if(Pattern.matches("^[A-Z]*\$", c.toString())) {
                return AppProperties.ENG_SIZE_UPPER
            }
            if(Pattern.matches("^[a-z]*\$", c.toString())) {
                return AppProperties.ENG_SIZE_LOWER
            }
            if(Pattern.matches("^[0-9]*\$", c.toString())) {
                return AppProperties.NUM_SIZE
            }

            return 1.0
        }
    }
}