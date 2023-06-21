package com.wooriyo.pinmenuer.common

import android.content.Context
import android.os.Bundle
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.databinding.DialogAlertBinding

class AlertDialog(context: Context, val content: String): BaseDialog(context) {
    lateinit var binding: DialogAlertBinding
    val TAG = "AlerDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.content.text = content
        binding.confirm.setOnClickListener { dismiss() }
    }
}