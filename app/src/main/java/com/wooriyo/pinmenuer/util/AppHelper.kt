package com.wooriyo.pinmenuer.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import java.text.DecimalFormat
import java.text.NumberFormat

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

        // 바깥 클릭했을 때 키보드 내리기
        fun hideKeyboard(activity: Activity, ev: MotionEvent) {
            val focusView = activity.currentFocus
            if (focusView != null) {
                val rect = Rect()
                focusView.getGlobalVisibleRect(rect)
                val x = ev.x.toInt()
                val y = ev.y.toInt()
                if (!rect.contains(x, y)) {
                    val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                    focusView.clearFocus()
                }
            }
        }

        //    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        //        val focusView = this.currentFocus
        //        if (focusView != null) {
        //            val rect = Rect()
        //            focusView.getGlobalVisibleRect(rect)
        //            val x = ev.x.toInt()
        //            val y = ev.y.toInt()
        //            if (!rect.contains(x, y)) {
        //                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
        //                focusView.clearFocus()
        //            }
        //        }
        //        return super.dispatchTouchEvent(ev)
        //    }

        val dec = DecimalFormat("00")
        val nformat = NumberFormat.getInstance()

        // 천 단위 콤마 찍기
        fun price(n: Int): String {
            return nformat.format(n)
        }

        // 한자리 수 n > 0n 형식으로 변환하기
        fun mkDouble(n: String): String {
            return dec.format(n)
        }

        fun osVersion(): Int = Build.VERSION.SDK_INT    // 안드로이드 버전
        fun versionName(context: Context): String = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        fun getPhoneModel(): String = Build.MODEL       // 디바이스 모델명

    }
}