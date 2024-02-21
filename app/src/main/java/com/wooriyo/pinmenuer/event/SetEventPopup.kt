package com.wooriyo.pinmenuer.event

import android.os.Bundle
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.databinding.ActivitySetEventPopupBinding

class SetEventPopup : BaseActivity() {
    lateinit var binding: ActivitySetEventPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetEventPopupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}