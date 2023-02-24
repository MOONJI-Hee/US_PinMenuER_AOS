package com.wooriyo.pinmenuer

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.wooriyo.pinmenuer.MyApplication.Companion.pref
import com.wooriyo.pinmenuer.util.AppHelper

open class BaseActivity: AppCompatActivity() {

    override fun onBackPressed() {
    }

    // 바깥화면 터치하면 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        AppHelper.hideKeyboard(this, currentFocus, ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        resources.displayMetrics.density = MyApplication.density
        resources.displayMetrics.densityDpi = MyApplication.dpi
        resources.displayMetrics.scaledDensity = MyApplication.density
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
    }
}