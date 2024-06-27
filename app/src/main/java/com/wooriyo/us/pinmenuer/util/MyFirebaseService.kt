package com.wooriyo.us.pinmenuer.util

import android.app.*
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sewoo.jpos.command.ESCPOSConst
import com.wooriyo.us.pinmenuer.BaseActivity.Companion.currentActivity
import com.wooriyo.us.pinmenuer.MyApplication
import com.wooriyo.us.pinmenuer.MyApplication.Companion.escposPrinter
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.StartActivity
import com.wooriyo.us.pinmenuer.config.AppProperties
import com.wooriyo.us.pinmenuer.config.AppProperties.Companion.CHANNEL_ID_CALL
import com.wooriyo.us.pinmenuer.config.AppProperties.Companion.CHANNEL_ID_ORDER
import com.wooriyo.us.pinmenuer.config.AppProperties.Companion.NOTIFICATION_ID_CALL
import com.wooriyo.us.pinmenuer.config.AppProperties.Companion.NOTIFICATION_ID_ORDER
import com.wooriyo.us.pinmenuer.history.ByHistoryActivity
import com.wooriyo.us.pinmenuer.history.ByTableActivity
import com.wooriyo.us.pinmenuer.history.TableHisActivity
import com.wooriyo.us.pinmenuer.model.ReceiptDTO
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

        if(currentActivity != null) {
            Log.d(TAG, "currentActivity.localClassName >> ${currentActivity.localClassName}")

            if(currentActivity.localClassName == "history.ByHistoryActivity") {
                if(message.data["chk_udt"] == "1") {
                    if(message.data["moredata"] == "call") {
                        (currentActivity as ByHistoryActivity).newCall()
                    }else if (message.data["reserv_type"] == "0") {
                        (currentActivity as ByHistoryActivity).newOrder()
                    } else {
                        (currentActivity as ByHistoryActivity).newReservation()
                    }
                }
            }else if(currentActivity.localClassName == "history.TableHisActivity") {
                if(message.data["chk_udt"] == "1") {
                    if(message.data["moredata"] == "call") {
                        (currentActivity as TableHisActivity).newCall()
                    }else if (message.data["reserv_type"] == "0") {
                        (currentActivity as TableHisActivity).newOrder()
                    } else {
                        (currentActivity as TableHisActivity).newReservation()
                    }
                }
            }else if(currentActivity.localClassName == "history.ByTableActivity") {
                if(message.data["chk_udt"] == "1") {
                    (currentActivity as ByTableActivity).getTableList()
                }
            }
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
                                    escposPrinter.printAndroidFont(pOrder,
                                        AppProperties.FONT_WIDTH, font_size, ESCPOSConst.LK_ALIGNMENT_LEFT)
                                }

                                if(result.reserType > 0 && result.rlist.isNotEmpty()) {
                                    val reserv = result.rlist[0]

                                    escposPrinter.lineFeed(2)

                                    escposPrinter.printAndroidFont("전화번호",
                                        AppProperties.FONT_WIDTH,
                                        20, ESCPOSConst.LK_ALIGNMENT_LEFT)
                                    escposPrinter.printAndroidFont(reserv.tel,
                                        AppProperties.FONT_WIDTH,
                                        33, ESCPOSConst.LK_ALIGNMENT_LEFT)
                                    escposPrinter.printAndroidFont("예약자명",
                                        AppProperties.FONT_WIDTH,
                                        20, ESCPOSConst.LK_ALIGNMENT_LEFT)
                                    escposPrinter.printAndroidFont(reserv.name,
                                        AppProperties.FONT_WIDTH,
                                        33, ESCPOSConst.LK_ALIGNMENT_LEFT)
                                    escposPrinter.printAndroidFont("요청사항",
                                        AppProperties.FONT_WIDTH,
                                        20, ESCPOSConst.LK_ALIGNMENT_LEFT)
                                    escposPrinter.printAndroidFont(reserv.memo,
                                        AppProperties.FONT_WIDTH,
                                        33, ESCPOSConst.LK_ALIGNMENT_LEFT)

                                    var str = ""
                                    when(result.reserType) {
                                        1 -> str = "매장"
                                        2 -> str = "포장"
                                    }
                                    escposPrinter.printAndroidFont(
                                        String.format(getString(R.string.reserv_date), str),
                                        AppProperties.FONT_WIDTH,
                                        20, ESCPOSConst.LK_ALIGNMENT_LEFT)

                                    escposPrinter.printAndroidFont(reserv.reserdt,
                                        AppProperties.FONT_WIDTH,
                                        33, ESCPOSConst.LK_ALIGNMENT_LEFT)
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
        val notificationManager = NotificationManagerCompat.from(this)

        var channelId = ""
        var notificationId = 0

        var sound = 0

        when(message.data["moredata"]) {
            "call" -> {
                channelId = CHANNEL_ID_CALL
                notificationId = NOTIFICATION_ID_CALL
                sound = R.raw.customcall
            }
            else -> {
                channelId = CHANNEL_ID_ORDER
                notificationId = NOTIFICATION_ID_ORDER
                sound = R.raw.customnoti
            }
        }

        val uri: Uri = Uri.parse("android.resource://com.wooriyo.us.pinmenuer/$sound")

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_noti)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(uri, AudioManager.STREAM_NOTIFICATION)
//            .setContentIntent(createPendingIntent())
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
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