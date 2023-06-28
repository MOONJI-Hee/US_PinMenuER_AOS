package com.wooriyo.pinmenuer.payment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.BaseActivity
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