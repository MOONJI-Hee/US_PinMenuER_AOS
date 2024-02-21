package com.wooriyo.pinmenuer.event

import android.os.Bundle
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityPreviewBinding

class PreviewActivity : BaseActivity() {

    lateinit var binding: ActivityPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}