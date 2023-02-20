package com.wooriyo.pinmenuer.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity() {
    lateinit var binding: ActivityMapBinding

    lateinit var lat : String
    lateinit var long: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lat = intent.getStringExtra("lat").toString()
        long = intent.getStringExtra("long").toString()

        if(lat.isEmpty() || long.isEmpty()) {
            getCurrenLoc()
        }
    }

    fun getCurrenLoc() {

    }
}