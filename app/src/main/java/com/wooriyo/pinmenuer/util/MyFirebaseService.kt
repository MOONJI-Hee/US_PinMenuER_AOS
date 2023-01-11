package com.wooriyo.pinmenuer.util

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.wooriyo.pinmenuer.MyApplication

class MyFirebaseService : FirebaseMessagingService() {
    val TAG = "MyFirebase"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        MyApplication.pref.setToken(token)
        Log.d(TAG, "new Token >> $token")
    }

//    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
//        if(message.notification != null) {
//            Log.d(TAG, "노티푸쉬 알람 ~!~!~!~! ${message.notification!!.title}")
//            Log.d(TAG, "노티푸쉬 알람 ~!~!~!~! ${message.notification!!.body}")
//
//            // 스크린 깨우기
//            val pm = this.getSystemService(POWER_SERVICE) as PowerManager
//            @SuppressLint("InvalidWakeLockTag") val sLock = pm.newWakeLock(
//                PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
//                "pinmenu"
//            )
//            sLock.acquire(5000)
//
//            val strPackage = "com.wooriyo.pinmenuee"
//            val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
//
//            val proceses = am.runningAppProcesses
//
//            var isForeground = false
//            for (process: ActivityManager.RunningAppProcessInfo? in proceses) {
//                if (process?.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    if (process.processName == strPackage) {
//                        isForeground = true
//                    }
//                }
//            }
//            if (isForeground)
//                Log.d(TAG, "어플 활성화 + 알림")
//            else
//                createNotification(message)
//        }
//    }
//
//    private fun createNotification(message: RemoteMessage) {
//        val channelId = "zorder_noti"
//
//        val builder = NotificationCompat.Builder(this, channelId)
////            .setSmallIcon(R.drawable.icon_noti)
//            .setLargeIcon( BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
//            .setContentTitle(message.notification!!.title)
//            .setContentText(message.notification!!.body)
//            .setContentIntent(createPendingIntent())
//            .setDefaults(Notification.DEFAULT_ALL)
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // 오레오 버전 이후에는 채널이 필요하다.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel =
//                NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_DEFAULT)
//            notificationManager.createNotificationChannel(channel)
//        }
//        // 알림 생성
//        notificationManager.notify(1, builder.build())
//    }
//
//    private fun createPendingIntent () : PendingIntent {
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//        val stackBuilder = TaskStackBuilder.create(this)
//        stackBuilder.addParentStack(MainActivity::class.java)
//        stackBuilder.addNextIntent(intent)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
//            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
//        else
//            return  stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT)
//    }

}