package com.wooriyo.pinmenuer.store

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.wooriyo.pinmenuer.databinding.ActivityStoreListBinding
import com.wooriyo.pinmenuer.util.AppHelper

class StoreListActivity : AppCompatActivity() {
    lateinit var binding : ActivityStoreListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
    }
}