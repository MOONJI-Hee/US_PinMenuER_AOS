package com.wooriyo.us.pinmenuer.util

import android.app.Activity
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import com.rt.printerlibrary.cmd.Cmd
import com.rt.printerlibrary.cmd.EscFactory
import com.rt.printerlibrary.enumerate.CommonEnum
import com.rt.printerlibrary.enumerate.ConnectStateEnum
import com.rt.printerlibrary.enumerate.ESCFontTypeEnum
import com.rt.printerlibrary.enumerate.SettingEnum
import com.rt.printerlibrary.factory.cmd.CmdFactory
import com.rt.printerlibrary.setting.TextSetting
import com.sewoo.jpos.command.ESCPOSConst
import com.wooriyo.us.pinmenuer.MyApplication
import com.wooriyo.us.pinmenuer.MyApplication.Companion.bluetoothAdapter
import com.wooriyo.us.pinmenuer.MyApplication.Companion.pairedDevices
import com.wooriyo.us.pinmenuer.MyApplication.Companion.remoteDevices
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.config.AppProperties
import com.wooriyo.us.pinmenuer.model.OrderDTO
import com.wooriyo.us.pinmenuer.model.OrderHistoryDTO
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.lang.reflect.Method
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern
import kotlin.math.floor

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
        fun price(d: Double): String {
            val nFormat = DecimalFormat("0.00")

//            val arr = d.toString().split(".")
//            arr[0].toInt()

//            val n = nFormat.format(d)
//            val n = floor(d)
//            val r = d.minus(n)
//
//            Log.d("AppHelper", "정수부분 >> $n")
//            Log.d("AppHelper", "실수부분 >> $r")
//
//            val strN = nformat.format(n)
//
//            val result = strN + r.toString()
//
//            Log.d("AppHelper", "최종 >> $result")

            return nFormat.format(d).toString()
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

        // 페어링 된 기기 찾기
        fun getPairedDevice() : Int {
            Log.d("AppHelper", "getPairedDevice 시작")
            pairedDevices.clear()
            remoteDevices.clear()

            val foundDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
            foundDevices?.forEach { device ->
                if(MyApplication.bluetoothPort.isValidAddress(device.address)) {
                    val deviceNum = device.bluetoothClass.majorDeviceClass

                    if(deviceNum == BluetoothClass.Device.Major.IMAGING) {
                        pairedDevices.add(device)
                        remoteDevices.add(device)
                    }
                }
            }
            Log.d("AppHelper", "페어링된 기기 목록 >> $pairedDevices")

            return if(pairedDevices.isNotEmpty()) 1 else 0
        }

        fun print(order: OrderHistoryDTO, context: Context) {
            if (MyApplication.rtPrinter.getPrinterInterface() != null && MyApplication.rtPrinter.connectState == ConnectStateEnum.Connected) {
                if(MyApplication.store.kitchen == "Y") printRT(order, context)
                if(MyApplication.store.receipt == "Y") printReceipt(order)
            }
            if(MyApplication.bluetoothPort.isConnected) {
                printSewoo(order, context)
            }
        }

        fun printRT(order: OrderHistoryDTO, context: Context) {
            val hyphen = StringBuilder()

            for (i in 1..48) {
                hyphen.append("-")
            }

            val escFac : CmdFactory = EscFactory()
            val escCmd : Cmd = escFac.create()
            escCmd.chartsetName = "UTF-8"

            val defaultText = TextSetting().apply {
                align = CommonEnum.ALIGN_LEFT
            }

            val smallText = TextSetting().apply {
                escFontType = ESCFontTypeEnum.FONT_A_12x24
                align = CommonEnum.ALIGN_LEFT
                isEscSmallCharactor = SettingEnum.Enable
            }

            val textSetting = TextSetting().apply {
                escFontType = ESCFontTypeEnum.FONT_A_12x24
                align = CommonEnum.ALIGN_LEFT
            }

            var title = "Product                                     Qty"

            if(MyApplication.store.fontsize == 1) {
                textSetting.doubleWidth = SettingEnum.Enable
                title = "Product              Qty"
            }

            try {
                escCmd.append(escCmd.getTextCmd(textSetting, MyApplication.store.name))
                escCmd.append(escCmd.lfcrCmd)
                escCmd.append(escCmd.getTextCmd(textSetting, "Order Date : ${order.regdt}"))
                escCmd.append(escCmd.lfcrCmd)
                escCmd.append(escCmd.getTextCmd(textSetting, "Order No   : ${order.ordcode}"))
                escCmd.append(escCmd.lfcrCmd)
                escCmd.append(escCmd.getTextCmd(textSetting, "Table No   : ${order.tableNo}\n"))
                escCmd.append(escCmd.lfcrCmd)

                escCmd.append(escCmd.getTextCmd(textSetting, title))
                escCmd.append(escCmd.lfcrCmd)

                escCmd.append(escCmd.getTextCmd(defaultText, hyphen.toString()))
                escCmd.append(escCmd.lfcrCmd)

                order.olist.forEach {
                    val pOrder = AppHelper.getPrintRT(it)
                    escCmd.append(escCmd.getTextCmd(textSetting, pOrder))
                    escCmd.append(escCmd.lfcrCmd)
                    escCmd.append(escCmd.getTextCmd(smallText, "\n"))
                }

                if(order.paytype == 3) {
                    escCmd.append(escCmd.getTextCmd(defaultText, hyphen.toString()))
                    escCmd.append(escCmd.lfcrCmd)
                    defaultText.doubleHeight = SettingEnum.Enable
                    escCmd.append(escCmd.getTextCmd(defaultText, "Complete payment"))
                    defaultText.doubleHeight = SettingEnum.Disable
                    escCmd.append(escCmd.lfcrCmd)
                }

                if(order.reserType > 0 && order.rlist.isNotEmpty()) {
                    val reserv = order.rlist[0]

                    escCmd.append(escCmd.getTextCmd(defaultText, hyphen.toString()))
                    escCmd.append(escCmd.lfcrCmd)

                    escCmd.append(escCmd.getTextCmd(smallText, "Phone Num"))
                    escCmd.append(escCmd.lfcrCmd)

                    escCmd.append(escCmd.getTextCmd(textSetting, reserv.tel))
                    escCmd.append(escCmd.lfcrCmd)

                    escCmd.append(escCmd.getTextCmd(smallText, "Res. Name"))
                    escCmd.append(escCmd.lfcrCmd)

                    escCmd.append(escCmd.getTextCmd(textSetting, reserv.name))
                    escCmd.append(escCmd.lfcrCmd)

                    if(reserv.memo.isNotEmpty()) {
                        escCmd.append(escCmd.getTextCmd(smallText, "Request"))
                        escCmd.append(escCmd.lfcrCmd)

                        escCmd.append(escCmd.getTextCmd(textSetting, reserv.memo))
                        escCmd.append(escCmd.lfcrCmd)
                    }

                    var str = ""
                    when(order.reserType) {
                        1 -> str = "Store"
                        2 -> str = "To-go"
                    }

                    escCmd.append(escCmd.getTextCmd(smallText, String.format(context.getString(R.string.reserv_date), str)))
                    escCmd.append(escCmd.lfcrCmd)

                    escCmd.append(escCmd.getTextCmd(textSetting, reserv.reserdt))
                    escCmd.append(escCmd.lfcrCmd)
                }

                escCmd.append(escCmd.cmdCutNew)

                MyApplication.rtPrinter.writeMsgAsync(escCmd.appendCmds)

            } catch (e : UnsupportedEncodingException) {
                e.printStackTrace()
                Log.d("AppHelper", "Exception > $e")

            }
        }

        // 주문내역(상세내역) 영수증 형태 String으로 받기 - RTP325
        fun getPrintRT(ord: OrderDTO) : String {
            val oneLine = AppProperties.RT_ONE_LINE
            val productLine = AppProperties.RT_PRODUCT
            val qtyLine = AppProperties.RT_QTY

            val result: StringBuilder = StringBuilder()
            val underline1 = StringBuilder()
            val underline2 = StringBuilder()
            val underline3 = StringBuilder()

            var total = 1
            ord.name.forEach {
                if (total <= productLine)
                    result.append(it)
                else if (total <= (productLine * 2))
                    underline1.append(it)
                else if (total <= (productLine * 3))
                    underline2.append(it)
                else
                    underline3.append(it)

                total++
            }

            val diff1 = productLine - (result.toString().length)
            for(i in 1..diff1) {
                result.append(" ")
            }

            result.append(" ")

            if (ord.gea < 100) result.append(" ")

            result.append(ord.gea)

            if(ord.gea < 10) result.append(" ")

            if (underline1.toString() != "")
                result.append("\r$underline1")

            if (underline2.toString() != "")
                result.append("\r$underline2")

            if(!ord.opt.isNullOrEmpty()) {
                ord.opt.forEach {
                    result.append("\n -$it")
                }
            }

            return result.toString()
        }

        fun printReceipt(order: OrderHistoryDTO) {
            val escFac : CmdFactory = EscFactory()
            val escCmd : Cmd = escFac.create()
            escCmd.chartsetName = "UTF-8"

            val defaultText = TextSetting().apply {
                align = CommonEnum.ALIGN_LEFT
            }

            val smallText = TextSetting().apply {
                escFontType = ESCFontTypeEnum.FONT_A_12x24
                align = CommonEnum.ALIGN_LEFT
                isEscSmallCharactor = SettingEnum.Enable
            }

            val titleSetting = TextSetting().apply {
                escFontType = ESCFontTypeEnum.FONT_A_12x24
                align = CommonEnum.ALIGN_MIDDLE
                doubleWidth = SettingEnum.Enable
                doubleHeight = SettingEnum.Enable
            }

            val hyphen = StringBuilder()

            for (i in 1..48) {
                hyphen.append("-")
            }

            try {
                // title (매장 이름, 매장 주소, 매장 번호)
                escCmd.append(escCmd.getTextCmd(titleSetting, MyApplication.store.name))
                escCmd.append(escCmd.lfcrCmd)
                escCmd.append(escCmd.getTextCmd(titleSetting, MyApplication.store.address))
                escCmd.append(escCmd.lfcrCmd)
                escCmd.append(escCmd.getTextCmd(titleSetting, MyApplication.store.tel))
                escCmd.append(escCmd.lfcrCmd)
                // 주문 날짜
                escCmd.append(escCmd.getTextCmd(defaultText, order.regdt))
                escCmd.append(escCmd.lfcrCmd)
                // 하이픈
                escCmd.append(escCmd.getTextCmd(defaultText, hyphen.toString()))
                escCmd.append(escCmd.lfcrCmd)

                // 주문내역
                order.olist.forEach {
                    val pOrder = getReceiptPrint(it)
                    escCmd.append(escCmd.getTextCmd(defaultText, pOrder))
                    escCmd.append(escCmd.lfcrCmd)
                    escCmd.append(escCmd.getTextCmd(smallText, "\n"))
                }

                // 하이픈
                escCmd.append(escCmd.getTextCmd(defaultText, hyphen.toString()))
                escCmd.append(escCmd.lfcrCmd)
                // 가격
                escCmd.append(escCmd.getTextCmd(defaultText, "SubTotal : $${price(order.amount)}"))
                escCmd.append(escCmd.lfcrCmd)
                escCmd.append(escCmd.getTextCmd(defaultText, "Tip : $${order.tip}"))
                escCmd.append(escCmd.lfcrCmd)
                escCmd.append(escCmd.getTextCmd(defaultText, "Tax : $${order.tax}\n"))
                escCmd.append(escCmd.lfcrCmd)
                // 총 가격
                escCmd.append(escCmd.getTextCmd(titleSetting, "Total : $${price(order.total_price)}\n"))
                escCmd.append(escCmd.lfcrCmd)

                escCmd.append(escCmd.cmdCutNew)

                MyApplication.rtPrinter.writeMsgAsync(escCmd.appendCmds)

            } catch (e : UnsupportedEncodingException) {
                e.printStackTrace()
                Log.d("AppHelper", "Exception > $e")
            }
        }

        // 주문내역(상세내역) 영수증 형태 String으로 받기 - RTP325
        fun getReceiptPrint(ord: OrderDTO) : String {
            val productLine = 40
            val amtLine = 6

            val result: StringBuilder = StringBuilder()
            val underline1 = StringBuilder()
            val underline2 = StringBuilder()
            val underline3 = StringBuilder()

            var total = 1
            ord.name.forEach {
                if (total <= productLine)
                    result.append(it)
                else if (total <= (productLine * 2))
                    underline1.append(it)
                else if (total <= (productLine * 3))
                    underline2.append(it)
                else
                    underline3.append(it)

                total++
            }

            val diff1 = productLine - (result.toString().length)
            for(i in 1..diff1) {
                result.append(" ")
            }

            result.append(" ")

            val diff2 = amtLine - price(ord.amount).length
            for(i in 1..diff2) {
                result.append(" ")
            }
            result.append("$")
            result.append(price(ord.amount))

            if (underline1.toString() != "")
                result.append("\r$underline1")

            if (underline2.toString() != "")
                result.append("\r$underline2")

            if(!ord.opt.isNullOrEmpty()) {
                ord.opt.forEach {
                    result.append("\n -$it")
                }
            }

            return result.toString()
        }

        // 주문 프린트
        fun printSewoo(order: OrderHistoryDTO, context: Context) {
            val hyphen = StringBuilder()                // 하이픈

            for (i in 1..AppProperties.HYPHEN_NUM) {
                hyphen.append("-")
            }

            val pOrderDt = order.regdt
            val pTableNo = order.tableNo
            val pOrderNo = order.ordcode

            if(MyApplication.bluetoothPort.isConnected){
                MyApplication.escposPrinter.printAndroidFont(
                    MyApplication.store.name,
                    AppProperties.FONT_WIDTH,
                    AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                MyApplication.escposPrinter.printAndroidFont("Order Date : $pOrderDt",
                    AppProperties.FONT_WIDTH,
                    AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                MyApplication.escposPrinter.printAndroidFont("Order No : $pOrderNo",
                    AppProperties.FONT_WIDTH,
                    AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                MyApplication.escposPrinter.printAndroidFont("Table No : $pTableNo",
                    AppProperties.FONT_WIDTH,
                    AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                MyApplication.escposPrinter.printAndroidFont(
                    AppProperties.TITLE_MENU,
                    AppProperties.FONT_WIDTH,
                    AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                MyApplication.escposPrinter.printAndroidFont(hyphen.toString(),
                    AppProperties.FONT_WIDTH, AppProperties.FONT_SIZE, ESCPOSConst.LK_ALIGNMENT_LEFT)

                order.olist.forEach {
                    val pOrder = getPrintSewoo(it)
                    MyApplication.escposPrinter.printAndroidFont(pOrder,
                        AppProperties.FONT_WIDTH, AppProperties.FONT_SIZE, ESCPOSConst.LK_ALIGNMENT_LEFT)
                }

                if(order.reserType > 0 && order.rlist.isNotEmpty()) {
                    val reserv = order.rlist[0]

                    MyApplication.escposPrinter.lineFeed(2)

                    MyApplication.escposPrinter.printAndroidFont("Phone Num",
                        AppProperties.FONT_WIDTH,
                        20, ESCPOSConst.LK_ALIGNMENT_LEFT)
                    MyApplication.escposPrinter.printAndroidFont(reserv.tel,
                        AppProperties.FONT_WIDTH,
                        33, ESCPOSConst.LK_ALIGNMENT_LEFT)
                    MyApplication.escposPrinter.printAndroidFont("Res. Name",
                        AppProperties.FONT_WIDTH,
                        20, ESCPOSConst.LK_ALIGNMENT_LEFT)
                    MyApplication.escposPrinter.printAndroidFont(reserv.name,
                        AppProperties.FONT_WIDTH,
                        33, ESCPOSConst.LK_ALIGNMENT_LEFT)
                    MyApplication.escposPrinter.printAndroidFont("Request",
                        AppProperties.FONT_WIDTH,
                        20, ESCPOSConst.LK_ALIGNMENT_LEFT)
                    MyApplication.escposPrinter.printAndroidFont(reserv.memo,
                        AppProperties.FONT_WIDTH,
                        33, ESCPOSConst.LK_ALIGNMENT_LEFT)

                    var str = ""
                    when(order.reserType) {
                        1 -> str = "Store"
                        2 -> str = "To-go"
                    }
                    MyApplication.escposPrinter.printAndroidFont(
                        String.format(context.getString(R.string.reserv_date), str),
                        AppProperties.FONT_WIDTH,
                        20, ESCPOSConst.LK_ALIGNMENT_LEFT)

                    MyApplication.escposPrinter.printAndroidFont(reserv.reserdt,
                        AppProperties.FONT_WIDTH,
                        33, ESCPOSConst.LK_ALIGNMENT_LEFT)
                }

                MyApplication.escposPrinter.lineFeed(4)
                MyApplication.escposPrinter.cutPaper()
            }
        }

        // 주문내역(상세내역) 영수증 형태 String으로 받기 - 세우전자
        fun getPrintSewoo(ord: OrderDTO) : String {
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
                    result.append("\n -$it")
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