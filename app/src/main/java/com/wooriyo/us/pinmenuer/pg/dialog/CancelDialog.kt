package com.wooriyo.us.pinmenuer.pg.dialog

import android.content.Context
import android.os.Bundle
import android.view.View.OnClickListener
import com.wooriyo.us.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogChoiceBinding

class CancelDialog(context: Context, val onClickListener: OnClickListener): BaseDialog(context) {
    lateinit var binding: DialogChoiceBinding
    val TAG = "PgCancelDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.content.text = context.getString(R.string.dialog_pg_cancel_info)

        binding.cancel.text = context.getString(R.string.payment_dialog_back)
        binding.confirm.text = context.getString(R.string.payment_cancel)

        binding.cancel.setOnClickListener { dismiss() }
        binding.confirm.setOnClickListener {
            dismiss()
            onClickListener.onClick(it)
        }
    }
}