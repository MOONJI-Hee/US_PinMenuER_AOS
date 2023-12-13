package com.wooriyo.pinmenuer

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.wooriyo.pinmenuer.listener.DialogListener
import com.wooriyo.pinmenuer.util.AppHelper

open class BaseDialog(context: Context) : Dialog(context) {
    val y = "Y"
    val n = "N"

    var useridx = 0

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        AppHelper.hideKeyboard(context, currentFocus, ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        useridx = MyApplication.pref.getUserIdx()
    }

    override fun show() {
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        super.show()
    }
}