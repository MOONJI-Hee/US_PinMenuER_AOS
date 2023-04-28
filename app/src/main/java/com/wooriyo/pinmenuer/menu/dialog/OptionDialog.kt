package com.wooriyo.pinmenuer.menu.dialog

import android.content.Context
import android.hardware.lights.LightsManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogOptionBinding
import com.wooriyo.pinmenuer.listener.DialogListener
import com.wooriyo.pinmenuer.menu.adpter.OptEditAdapter
import com.wooriyo.pinmenuer.model.OptionDTO

class OptionDialog(context: Context, val type: Int, val option : OptionDTO): BaseDialog(context) {  // TODO 다이얼로그 생성할 때 수정은 깊은 복사 진행
    lateinit var binding:DialogOptionBinding
    lateinit var dialogListener: DialogListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window = window ?: return
        val params = window.attributes
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = params

        when(type) {    // 0: 선택 옵션, 1: 필수 옵션
            1 -> {
            }
            0 -> {
                binding.run {
                    title.text = context.getString(R.string.option_choice)
                    optNameTitle.text = context.getString(R.string.opt_chc_name)
                    optInfo1.text = context.getString(R.string.opt_chc_info1)
                    optInfo2.text = context.getString(R.string.opt_chc_info2)
                }
            }
        }

        binding.rvOptVal.run {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = OptEditAdapter(option)
        }

        binding.close.setOnClickListener { dismiss() }
        binding.save.setOnClickListener {
            option.name = binding.optName.text.toString()
            Log.d("OptionDialog", "옵션 저장 ~!~!!~ >> $option")

            if(option.name.isEmpty()) {
                Toast.makeText(context, R.string.opt_name_hint, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dialogListener.onOptAdd(option)
            dismiss()
        }
    }

    fun setOnDialogListener(dialogListener: DialogListener) {
        this.dialogListener = dialogListener
    }
}