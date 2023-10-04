package com.wooriyo.pinmenuer.payment.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogNoticeBinding

class PgBlinkDialog(context: Context): BaseDialog(context) {
    lateinit var binding: DialogNoticeBinding
    val TAG = "PgBlinkDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.title.visibility = View.GONE
        binding.close.visibility = View.GONE
        binding.content.text = context.getString(R.string.payment_set_pg_dialog_blink)
        binding.confirm.text = context.getString(R.string.payment_dialog_back)

        binding.confirm.setOnClickListener { dismiss() }
    }
}