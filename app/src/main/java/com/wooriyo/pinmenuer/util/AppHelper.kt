package com.wooriyo.pinmenuer.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import java.text.DecimalFormat

// 자주 쓰는 메소드 모음 - 문지희 (2022.10 갱신)
class AppHelper {
    companion object {

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

        val dec = DecimalFormat("#,##0")

        // 천 단위 콤마 찍기
        fun price(n: Int): String {
            return dec.format(n)
        }

        fun osVersion(): Int = Build.VERSION.SDK_INT    // 안드로이드 버전
        fun versionName(context: Context): String = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        fun getPhoneModel(): String = Build.MODEL       // 디바이스 모델명
    }
}