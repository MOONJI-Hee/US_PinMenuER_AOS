package com.wooriyo.pinmenuer.call

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityOrderListBinding

class CallListActivity : AppCompatActivity() {
    lateinit var binding: ActivityOrderListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.title.text = getString(R.string.call)
        binding.btnManage.text = getString(R.string.call_manage)
    }
}