package com.wooriyo.pinmenuer.qrcode

import android.os.Bundle
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.databinding.ActivityPgJudgeBinding

class PgJudgeActivity : BaseActivity() {
    lateinit var binding: ActivityPgJudgeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPgJudgeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}