package com.wooriyo.pinmenuer.store

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.ActivityStoreSetDetailBinding

class StoreSetDetailActivity : AppCompatActivity() {
    lateinit var binding : ActivityStoreSetDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreSetDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}