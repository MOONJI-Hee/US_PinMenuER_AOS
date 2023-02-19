package com.wooriyo.pinmenuer.call

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.ActivityCallSetBinding

class CallSetActivity : AppCompatActivity() {
    lateinit var binding: ActivityCallSetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallSetBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}