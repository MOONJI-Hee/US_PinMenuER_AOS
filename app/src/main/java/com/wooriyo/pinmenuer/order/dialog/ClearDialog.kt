package com.wooriyo.pinmenuer.order.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogClearBinding

class ClearDialog(context: Context, val type: String, val onClickListener: View.OnClickListener): BaseDialog(context) {
    lateinit var binding: DialogClearBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogClearBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(type == "call") {
            binding.content.text = context.getString(R.string.call_dialog_clear)
        }

        binding.close.setOnClickListener { dismiss() }
        binding.confirm.setOnClickListener{ dismiss() }
        binding.clear.setOnClickListener {
            onClickListener.onClick(it)
        }
    }

}