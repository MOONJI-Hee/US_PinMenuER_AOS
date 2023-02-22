package com.wooriyo.pinmenuer.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.ActivityMapBinding
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapActivity : AppCompatActivity() {
    lateinit var binding: ActivityMapBinding

    private lateinit var mapView : MapView

    var lat : Double = 35.816               //우리요 위도
    var long: Double = 217.1075         //우리요 경도

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = MapView(this@MapActivity)
        mapView.setZoomLevel(7, true)

        binding.map.addView(mapView)

        val strLat = intent.getStringExtra("lat")
        val strLong = intent.getStringExtra("long")

        if(!strLat.isNullOrEmpty() && !strLong.isNullOrEmpty()) {
            lat = strLat.toDouble()
            long = strLong.toDouble()
        }
        setLocation()
    }

    fun getCurrenLoc() {

    }

    private fun setLocation() {
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat, long), false)
    }
}