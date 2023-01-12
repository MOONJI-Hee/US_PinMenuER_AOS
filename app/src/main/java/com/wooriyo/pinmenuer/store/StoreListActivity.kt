package com.wooriyo.pinmenuer.store

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.wooriyo.pinmenuer.databinding.ActivityStoreListBinding

class StoreListActivity : AppCompatActivity() {
    lateinit var binding : ActivityStoreListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}