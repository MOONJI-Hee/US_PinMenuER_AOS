package com.wooriyo.pinmenuer.store

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.ActivityStoreSetDetailBinding
import com.wooriyo.pinmenuer.util.AppHelper

class StoreSetDetailActivity : AppCompatActivity() {
    lateinit var binding : ActivityStoreSetDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreSetDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
    }
}