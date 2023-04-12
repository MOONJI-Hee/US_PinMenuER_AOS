package com.wooriyo.pinmenuer.member

import android.os.Bundle
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityFindPwdBinding

class FindPwdActivity : BaseActivity() {
    lateinit var binding: ActivityFindPwdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindPwdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener { finish() }
        binding.save.setOnClickListener {
            val email = binding.etId.text.toString()
            if(email.isEmpty()) {
                Toast.makeText(this@FindPwdActivity, R.string.email_hint, Toast.LENGTH_SHORT).show()
            }else {

            }
        }
    }
}