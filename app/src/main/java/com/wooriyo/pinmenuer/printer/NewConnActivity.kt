package com.wooriyo.pinmenuer.printer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.ActivityNewConnBinding

class NewConnActivity : AppCompatActivity() {
    lateinit var binding: ActivityNewConnBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewConnBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}