package com.wooriyo.pinmenuer.order.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.databinding.DialogClearBinding

class ClearDialog(context: Context, val onClickListener: View.OnClickListener): BaseDialog(context) {
    lateinit var binding: DialogClearBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogClearBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.close.setOnClickListener { dismiss() }
        binding.confirm.setOnClickListener{ dismiss() }
        binding.clear.setOnClickListener {
            onClickListener.onClick(it)
        }
    }

}