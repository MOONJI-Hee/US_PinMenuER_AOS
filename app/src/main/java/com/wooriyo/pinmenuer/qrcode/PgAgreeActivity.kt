package com.wooriyo.pinmenuer.qrcode

import android.os.Bundle
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.databinding.ActivityPgAgreeBinding

class PgAgreeActivity : BaseActivity() {
    lateinit var binding: ActivityPgAgreeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPgAgreeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}