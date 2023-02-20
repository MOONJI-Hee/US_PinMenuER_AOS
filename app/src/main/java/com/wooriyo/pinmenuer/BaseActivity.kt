package com.wooriyo.pinmenuer

import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.wooriyo.pinmenuer.util.AppHelper

class BaseActivity: AppCompatActivity() {

    override fun onBackPressed() {
    }

    // 바깥화면 터치하면 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        AppHelper.hideKeyboard(this, currentFocus, ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
    }
}