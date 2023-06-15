package com.wooriyo.pinmenuer.printer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.ActivitySetConnBinding

class setConnActivity : AppCompatActivity() {
    lateinit var binding: ActivitySetConnBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetConnBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}