package com.wooriyo.pinmenuer.pg.dialog

import android.content.Context
import android.os.Bundle
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.databinding.DialogNoPgInfoBinding

class NoPgInfoDialog(context: Context): BaseDialog(context) {
    lateinit var binding: DialogNoPgInfoBinding
    val TAG = "NoPgInfoDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogNoPgInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.close.setOnClickListener { dismiss() }
        binding.confirm.setOnClickListener { dismiss() }
    }
}