package com.wooriyo.pinmenuer.store

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wooriyo.pinmenuer.databinding.ActivityStoreSetBinding

class StoreSetActivity : AppCompatActivity() {
    lateinit var binding : ActivityStoreSetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreSetBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}