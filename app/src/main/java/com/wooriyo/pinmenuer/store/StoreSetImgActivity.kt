package com.wooriyo.pinmenuer.store

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.wooriyo.pinmenuer.databinding.ActivityStoreSetImgBinding
import com.wooriyo.pinmenuer.util.AppHelper

class StoreSetImgActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding : ActivityStoreSetImgBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreSetImgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.regImg.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.back -> finish()
            binding.save -> save()
            binding.regImg -> storagePermission()
        }
    }

    private fun save() {

    }

    fun storagePermission() {

    }
}