package com.wooriyo.pinmenuer.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wooriyo.pinmenuer.BaseDialogFragment
import com.wooriyo.pinmenuer.databinding.DialogConfirmBinding

class ConfirmDialog(val title: String, val content: String, val btn: String, val onClickListener: View.OnClickListener): BaseDialogFragment() {
    lateinit var binding: DialogConfirmBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogConfirmBinding.inflate(layoutInflater)

        if(title.isNotEmpty()) {
            binding.title.text = title
            binding.title.visibility = View.VISIBLE
        }

        if(content.isEmpty()) {
            binding.content.visibility = View.GONE
        }

        binding.content.text = content
        binding.confirm.text = btn

        binding.cancel.setOnClickListener { dismiss() }
        binding.confirm.setOnClickListener {
            onClickListener.onClick(it)
            dismiss()
        }

        return binding.root
    }
}