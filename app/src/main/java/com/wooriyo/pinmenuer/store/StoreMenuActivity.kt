package com.wooriyo.pinmenuer.store

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.ActivityStoreMenuBinding

class StoreMenuActivity : AppCompatActivity() {
    lateinit var binding: ActivityStoreMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}