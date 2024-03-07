package com.wooriyo.pinmenuer

import android.app.Activity
import android.app.ActivityManager
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.wooriyo.pinmenuer.common.LoadingDialog
import com.wooriyo.pinmenuer.util.AppHelper

open class BaseActivity: AppCompatActivity() {
    open lateinit var mActivity: Activity
    open lateinit var TAG: String
    open lateinit var loadingDialog : LoadingDialog

    val y = "Y"
    val n = "N"
    val d = "D"

    companion object {
        lateinit var currentActivity: Activity
    }

    override fun onBackPressed() {}

    // 바깥화면 터치하면 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        AppHelper.hideKeyboard(this, currentFocus, ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        resources.displayMetrics.density = MyApplication.density
        resources.displayMetrics.densityDpi = MyApplication.dpi
        resources.displayMetrics.scaledDensity = MyApplication.density

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        mActivity = this
        TAG = mActivity.localClassName

        loadingDialog = LoadingDialog()

        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
        currentActivity = this
    }
}