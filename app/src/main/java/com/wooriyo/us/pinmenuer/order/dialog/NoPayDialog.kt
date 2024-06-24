package com.wooriyo.us.pinmenuer.order.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.wooriyo.us.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogNoPayBinding
import com.wooriyo.us.pinmenuer.payment.SetPayActivity

class NoPayDialog(context: Context, val type: Int): BaseDialog(context) {
    lateinit var binding: DialogNoPayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogNoPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var title = ""
        var content = ""

        when(type) {
            0 -> {  // QR일 때
                title = context.getString(R.string.order_pay_qr)
                content = context.getString(R.string.order_enable_qr)
            }
            1 -> {
                title = context.getString(R.string.title_card)
                content = context.getString(R.string.order_enable_card)
            }
        }

        binding.title.text = title
        binding.content.text = content

        binding.close.setOnClickListener { dismiss() }
        binding.go.setOnClickListener {
            context.startActivity(Intent(context, SetPayActivity::class.java))
            dismiss()
        }
    }
}