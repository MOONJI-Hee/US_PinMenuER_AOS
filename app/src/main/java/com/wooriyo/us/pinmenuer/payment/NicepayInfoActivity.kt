package com.wooriyo.us.pinmenuer.payment

import android.content.Intent
import android.os.Bundle
import com.wooriyo.us.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.databinding.ActivityNicepayInfoBinding

class NicepayInfoActivity : BaseActivity() {
    lateinit var binding: ActivityNicepayInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNicepayInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fromOrder = intent.getStringExtra("fromOrder") ?: ""

        binding.back.setOnClickListener { finish() }
        binding.btnNicepay.setOnClickListener {
            val intent = Intent(this@NicepayInfoActivity, SetPgInfoActivity::class.java)
            intent.putExtra("fromOrder", fromOrder)
            startActivity(intent)
        }
    }
}