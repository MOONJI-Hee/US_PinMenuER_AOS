package com.wooriyo.pinmenuer.order.dialog

import android.content.Context
import android.os.Bundle
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.databinding.DialogCompleteBinding
import com.wooriyo.pinmenuer.listener.DialogListener

class CompleteDialog(context: Context): BaseDialog(context) {
    lateinit var binding: DialogCompleteBinding
    lateinit var dialogListener: DialogListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.close.setOnClickListener { dismiss() }
        binding.complete.setOnClickListener {
            val popup = if(binding.visibility.isChecked) 1 else 0

            dialogListener.onComplete(popup)
            dismiss()
        }
    }

    fun setOnCompleteListener(dialogListener: DialogListener)  {
        this.dialogListener = dialogListener
    }
}