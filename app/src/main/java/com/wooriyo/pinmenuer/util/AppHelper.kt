package com.wooriyo.pinmenuer.util

import android.content.Context
import android.os.Build
import java.text.DecimalFormat

// 자주 쓰는 메소드 모음 - 문지희 (2022.10 갱신)
class AppHelper {
    companion object {

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