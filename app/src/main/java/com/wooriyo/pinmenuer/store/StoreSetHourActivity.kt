package com.wooriyo.pinmenuer.store

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.ActivityStoreSetHourBinding

class StoreSetHourActivity : AppCompatActivity() {
    lateinit var binding : ActivityStoreSetHourBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreSetHourBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}