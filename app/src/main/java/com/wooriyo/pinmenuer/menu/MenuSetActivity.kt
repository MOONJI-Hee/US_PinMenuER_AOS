package com.wooriyo.pinmenuer.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.ActivityMenuSetBinding

class MenuSetActivity : AppCompatActivity() {
    lateinit var binding: ActivityMenuSetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuSetBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}