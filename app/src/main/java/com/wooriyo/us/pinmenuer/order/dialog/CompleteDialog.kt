package com.wooriyo.us.pinmenuer.order.dialog

import android.content.Context
import android.os.Bundle
import com.wooriyo.us.pinmenuer.BaseDialog
import com.wooriyo.us.pinmenuer.databinding.DialogCompleteBinding
import com.wooriyo.us.pinmenuer.listener.DialogListener

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