package com.wooriyo.pinmenuer.store

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.ActivityStoreSetHourBinding
import com.wooriyo.pinmenuer.util.AppHelper

class StoreSetHourActivity : AppCompatActivity() {
    lateinit var binding : ActivityStoreSetHourBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreSetHourBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
    }
}