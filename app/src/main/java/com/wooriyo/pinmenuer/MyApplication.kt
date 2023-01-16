package com.wooriyo.pinmenuer

import android.app.Application
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import com.wooriyo.pinmenuer.db.AppDatabase
import com.wooriyo.pinmenuer.model.SharedDTO

class MyApplication: Application() {
    companion object {
        lateinit var pref: SharedDTO
        lateinit var db : AppDatabase

        var density = 1.0F
        var dpi = 160

        var osver = 0
        lateinit var appver : String
        lateinit var md : String
    }

    override fun onCreate() {
        pref = SharedDTO(applicationContext)
        db = AppDatabase.getInstance(applicationContext)

        density = resources.displayMetrics.density
        dpi = resources.displayMetrics.densityDpi

        osver = Build.VERSION.SDK_INT
        appver = applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0).versionName
        md = Build.MODEL

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