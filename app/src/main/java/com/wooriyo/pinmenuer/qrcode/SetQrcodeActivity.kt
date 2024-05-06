package com.wooriyo.pinmenuer.qrcode

import android.os.Bundle
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.databinding.ActivitySetQrcodeBinding

class SetQrcodeActivity : BaseActivity() {
    lateinit var binding: ActivitySetQrcodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}