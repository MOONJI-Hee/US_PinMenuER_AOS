package com.wooriyo.pinmenuer.store

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.ActivityStoreSetImgBinding

class StoreSetImgActivity : AppCompatActivity() {
    lateinit var binding : ActivityStoreSetImgBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreSetImgBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}