package com.wooriyo.pinmenuer.store

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.ActivityStoreMenuBinding
import com.wooriyo.pinmenuer.util.AppHelper

class StoreMenuActivity : AppCompatActivity() {
    lateinit var binding: ActivityStoreMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
    }

}