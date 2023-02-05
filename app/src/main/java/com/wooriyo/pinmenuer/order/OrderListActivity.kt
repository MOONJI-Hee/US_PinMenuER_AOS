package com.wooriyo.pinmenuer.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.ActivityOrderListBinding

class OrderListActivity : AppCompatActivity() {
    lateinit var binding: ActivityOrderListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}