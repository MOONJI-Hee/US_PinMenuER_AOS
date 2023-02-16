package com.wooriyo.pinmenuer.menu.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.DialogOptionBinding

class OptionDialog(context: Context, val type: Int): Dialog(context) {
    lateinit var binding:DialogOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when(type) {    // 1: 필수 옵션 , 2: 선택 옵션
            1 -> {}
            2 -> {}
        }
    }
}