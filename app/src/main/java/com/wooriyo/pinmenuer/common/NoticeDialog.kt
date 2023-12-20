package com.wooriyo.pinmenuer.common

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.databinding.DialogNoticeBinding

class NoticeDialog(context: Context, val title: String, val content: String, private val onClickListener: OnClickListener): BaseDialog(context) {
    lateinit var binding: DialogNoticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(title.isEmpty()) {
            binding.title.visibility = View.GONE
        }
        binding.title.text = title
        binding.content.text = content

        binding.close.setOnClickListener { dismiss() }
        binding.confirm.setOnClickListener {
            dismiss()
            onClickListener.onClick(it)
        }
    }
}