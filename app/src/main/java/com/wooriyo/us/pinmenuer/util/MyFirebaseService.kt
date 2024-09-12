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
import com.wooriyo.us.pinmenuer.model.OrderHistoryDTO
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
            Log.d(TAG, "currentActivity.localClassName >> ${currentActivity!!.localClassName}")

            if(currentActivity!!.localClassName == "history.ByHistoryActivity") {
                if(message.data["chk_udt"] == "1") {
                    if(message.data["moredata"] == "call") {
                        (currentActivity as ByHistoryActivity).newCall()
                    }else if (message.data["reserv_type"] == "0") {
                        (currentActivity as ByHistoryActivity).newOrder()
                    } else {
                        (currentActivity as ByHistoryActivity).newReservation()
                    }
                }
            }else if(currentActivity!!.localClassName == "history.TableHisActivity") {
                if(message.data["chk_udt"] == "1") {
                    if(message.data["moredata"] == "call") {
                        (currentActivity as TableHisActivity).newCall()
                    }else if (message.data["reserv_type"] == "0") {
                        (currentActivity as TableHisActivity).newOrder()
                    } else {
                        (currentActivity as TableHisActivity).newReservation()
                    }
                }
            }else if(currentActivity!!.localClassName == "history.ByTableActivity") {
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

            ApiClient.service.getReceipt(ordCode_key.toString()).enqueue(object : retrofit2.Callback<OrderHistoryDTO>{
                override fun onResponse(call: Call<OrderHistoryDTO>, response: Response<OrderHistoryDTO>) {
                    Log.d(TAG, "단건 주문 조회 URL : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return

                    when(result.status) {
                        1 -> {
                            result.ordcode = ordCode ?: ""
                            PrinterHelper.print(result, applicationContext)
                        }
                        else -> Toast.makeText(applicationContext, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<OrderHistoryDTO>, t: Throwable) {
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