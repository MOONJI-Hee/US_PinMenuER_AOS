package com.wooriyo.us.pinmenuer.payment

import android.os.Bundle
import com.wooriyo.us.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.databinding.ActivityReaderModelBinding

class ReaderModelActivity : BaseActivity() {
    lateinit var binding: ActivityReaderModelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReaderModelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener { finish() }
    }
}