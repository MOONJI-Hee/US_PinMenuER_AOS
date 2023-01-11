package com.wooriyo.pinmenuer

import android.app.Application
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import com.wooriyo.pinmenuer.model.SharedDTO

class MyApplication: Application() {
    companion object {
        lateinit var pref: SharedDTO
        var density = 1.0F
        var dpi = 160
    }

    override fun onCreate() {
        pref = SharedDTO(applicationContext)

        density = resources.displayMetrics.density
        dpi = resources.displayMetrics.densityDpi

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        var width = 0
        var height = 0

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            width = windowMetrics.bounds.width()
            height = windowMetrics.bounds.height()
        } else {
            val display = windowManager.defaultDisplay
            val realpoint = Point()
            display.getRealSize(realpoint) // or getSize(size)
            width = realpoint.x
            height = realpoint.y
        }

        if (width <= 1280) {
            density = 1.0F
            dpi = 160
        }

        super.onCreate()
    }
}