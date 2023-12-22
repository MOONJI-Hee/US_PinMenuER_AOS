package com.wooriyo.pinmenuer.util

import android.annotation.SuppressLint
import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager.STREAM_NOTIFICATION
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sewoo.jpos.command.ESCPOSConst
import com.wooriyo.pinmenuer.BaseActivity.Companion.currentActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.MyApplication.Companion.escposPrinter
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.StartActivity
import com.wooriyo.pinmenuer.call.CallListActivity
import com.wooriyo.pinmenuer.config.AppProperties
import com.wooriyo.pinmenuer.config.AppProperties.Companion.CHANNEL_ID_ORDER
import com.wooriyo.pinmenuer.config.AppProperties.Companion.NOTIFICATION_ID_ORDER
import com.wooriyo.pinmenuer.model.ReceiptDTO
import com.wooriyo.pinmenuer.order.OrderListActivity
import retrofit2.Call
import retrofit2.Response


class MyFirebaseService : FirebaseMessagingService() {
    val TAG = "MyFirebase"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        MyApplication.pref.setToken(token)
        Log.d(TAG, "new Token >> $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "message.data >> ${message.data}")
        Log.d(TAG, "message.notification.body >> ${message.notification?.body}")

        createNotification(message)

        //알림음 재생
        val sound = R.raw.customnoti
        val uri: Uri = Uri.parse("android.resource://com.wooriyo.pinmenuer/$sound")
        val ringtone = RingtoneManager.getRingtone(applicationContext, uri)

        if(currentActivity != null) {
            Log.d(TAG, "currentActivity.localClassName >> ${currentActivity.localClassName}")

//            if (currentActivity.localClassName == "call.CallListActivity")
//                (currentActivity as CallListActivity).getCallList()
//            else if (currentActivity.localClassName == "order.OrderListActivity")
//                (currentActivity as OrderListActivity).getOrderList()
        }else{
            Log.d(TAG, "currentActivity == null")
        }

        if(message.data["moredata"] == "call") {
            // 호출
        }else {
            val ordCode_key = message.data["moredata"]
            val ordCode = message.data["moredata_ordcode"]

//        val storeidx = message.data["storeidx"]

            ApiClient.service.getReceipt(ordCode_key.toString()).enqueue(object : retrofit2.Callback<ReceiptDTO>{
                override fun onResponse(call: Call<ReceiptDTO>, response: Response<ReceiptDTO>) {
                    Log.d(TAG, "단건 주문 조회 URL : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return

                    when(result.status) {
                        1 -> {
                            if(MyApplication.bluetoothPort.isConnected) {
                                val pOrderDt = result.regdt
                                val pTableNo = result.tableNo
                                val pOrderNo = ordCode

                                val hyphen_num = AppProperties.HYPHEN_NUM_BIG
                                val font_size = AppProperties.FONT_BIG

                                val hyphen = StringBuilder()    // 하이픈
                                for (i in 1..hyphen_num) {
                                    hyphen.append("-")
                                }

                                escposPrinter.printAndroidFont(
                                    result.storenm,
                                    AppProperties.FONT_WIDTH,
                                    AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                                escposPrinter.printAndroidFont("주문날짜 : $pOrderDt",
                                    AppProperties.FONT_WIDTH,
                                    AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                                escposPrinter.printAndroidFont("주문번호 : $pOrderNo",
                                    AppProperties.FONT_WIDTH,
                                    AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                                escposPrinter.printAndroidFont("테이블번호 : $pTableNo",
                                    AppProperties.FONT_WIDTH,
                                    AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                                escposPrinter.printAndroidFont(
                                    AppProperties.TITLE_MENU,
                                    AppProperties.FONT_WIDTH, AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                                escposPrinter.printAndroidFont(hyphen.toString(),
                                    AppProperties.FONT_WIDTH, font_size, ESCPOSConst.LK_ALIGNMENT_LEFT)

                                result.orderdata.forEach {
                                    val pOrder = AppHelper.getPrint(it)
                                    escposPrinter.printAndroidFont(pOrder,AppProperties.FONT_WIDTH, font_size, ESCPOSConst.LK_ALIGNMENT_LEFT)
                                }
                                escposPrinter.lineFeed(4)
                                escposPrinter.cutPaper()
                            }else {
                                Log.d(TAG, "프린트 연결 안됨")
                            }
                        }
                        else -> Toast.makeText(applicationContext, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ReceiptDTO>, t: Throwable) {
                    Toast.makeText(applicationContext, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "단건 주문 조회 오류 >> $t")
                    Log.d(TAG, "단건 주문 조회 오류 >> ${call.request()}")
                }
            })
        }
    }

    private fun createNotification(message: RemoteMessage) {
        val sound = R.raw.customcall
        val uri: Uri = Uri.parse("android.resource://com.wooriyo.pinmenuer/$sound")

        val builder = NotificationCompat.Builder(this, CHANNEL_ID_ORDER)
            .setSmallIcon(R.drawable.ic_noti)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setSound(uri, STREAM_NOTIFICATION)
//            .setContentIntent(createPendingIntent())
            .setAutoCancel(true)

        // 알림 생성
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFICATION_ID_ORDER, builder.build())
    }

    private fun createPendingIntent () : PendingIntent {
        val intent = Intent(this, StartActivity::class.java)

        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(StartActivity::class.java)
        stackBuilder.addNextIntent(intent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        else
            return  stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT)
    }
}