package com.wooriyo.pinmenuer.common

import android.content.Context
import android.os.Bundle
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.databinding.DialogNoticeBinding

class NoticeDialog(context: Context, val content: String): BaseDialog(context) {
    lateinit var binding: DialogNoticeBinding
    val TAG = "NoticeDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.content.text = content
        binding.confirm.setOnClickListener { dismiss() }
    }
}