package com.wooriyo.pinmenuer.call

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.ActivityCallListBinding
import com.wooriyo.pinmenuer.databinding.ActivityMapBinding

class CallListActivity : AppCompatActivity() {
    lateinit var binding: ActivityCallListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}