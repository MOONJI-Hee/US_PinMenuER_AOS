package com.wooriyo.pinmenuer.common

import android.content.Context
import android.os.Bundle
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.databinding.DialogInfoBinding

class InfoDialog(context: Context, val title: String, val content: String): BaseDialog(context) {
    lateinit var binding: DialogInfoBinding

    val TAG = "InfoDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.title.text = title
        binding.content.text = content

        binding.close.setOnClickListener { dismiss() }
    }
}