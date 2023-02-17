package com.wooriyo.pinmenuer.menu.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogOptionBinding
import com.wooriyo.pinmenuer.model.OptionDTO

class OptionDialog(context: Context, val type: Int, val option : OptionDTO?): Dialog(context) {
    lateinit var binding:DialogOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when(type) {    // 1: 필수 옵션 , 2: 선택 옵션
            1 -> {
            }
            2 -> {
                binding.run {
                    title.text = context.getString(R.string.option_choice)
                    optNameTitle.text = context.getString(R.string.opt_chc_name)
                    optInfo1.text = context.getString(R.string.opt_chc_info1)
                    optInfo2.text = context.getString(R.string.opt_chc_info2)
                }
            }
        }

        if(option == null) {

        }
    }
}