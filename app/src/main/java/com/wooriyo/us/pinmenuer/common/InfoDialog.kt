package com.wooriyo.us.pinmenuer.common

import android.content.Context
import android.os.Bundle
import android.view.View
import com.wooriyo.us.pinmenuer.BaseDialog
import com.wooriyo.us.pinmenuer.databinding.DialogInfoBinding

class InfoDialog(context: Context, val title: String, val content: String): BaseDialog(context) {
    lateinit var binding: DialogInfoBinding

    val TAG = "InfoDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(title.isEmpty()) {
            binding.title.visibility = View.GONE
        }

        binding.title.text = title
        binding.content.text = content

        binding.close.setOnClickListener { dismiss() }
    }
}