package com.wooriyo.us.pinmenuer.pg.dialog

import android.content.Context
import android.os.Bundle
import com.wooriyo.us.pinmenuer.BaseDialog
import com.wooriyo.us.pinmenuer.databinding.DialogNoPgInfoBinding

class NoPgInfoDialog(context: Context): BaseDialog(context) {
    lateinit var binding: DialogNoPgInfoBinding
    val TAG = "NoPgInfoDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogNoPgInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.confirm.setOnClickListener { dismiss() }
    }
}