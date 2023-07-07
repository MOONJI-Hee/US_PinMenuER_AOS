package com.wooriyo.pinmenuer.menu.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogOptionBinding
import com.wooriyo.pinmenuer.listener.DialogListener
import com.wooriyo.pinmenuer.menu.adpter.OptEditAdapter
import com.wooriyo.pinmenuer.model.OptionDTO
import java.util.*

class OptionDialog(context: Context, val position: Int, private val option : OptionDTO): BaseDialog(context) {  // TODO 다이얼로그 생성할 때 수정은 깊은 복사 진행
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

        binding.run {
            if(position == -1) {    // 옵션 추가
                if(option.optreq == 0) {
                    title.text = context.getString(R.string.option_choice)
                    optNameTitle.text = context.getString(R.string.opt_chc_name)
                    optInfo1.text = context.getString(R.string.opt_chc_info1)
                    optInfo2.text = context.getString(R.string.opt_chc_info2)
                }
            }else {                // 옵션 수정
                optInfo1.text = context.getString(R.string.opt_udt_info1)
                optInfo2.text = context.getString(R.string.opt_udt_info2)

                when(option.optreq) {   // 0: 선택 옵션, 1: 필수 옵션
                    1 -> title.text = context.getString(R.string.opt_udt_req)
                    0 -> {
                        title.text = context.getString(R.string.opt_udt_chc)
                        optNameTitle.text = context.getString(R.string.opt_chc_name)
                    }
                }
                optName.setText(option.name)
                delete.visibility = View.VISIBLE
            }
        }

        binding.rvOptVal.run {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = OptEditAdapter(option)
        }

        binding.close.setOnClickListener { dismiss() }
        binding.save.setOnClickListener {
            option.name = binding.optName.text.toString()
            Collections.replaceAll(option.optpay, "", "0")

            Log.d("OptionDialog", "옵션 저장 ~!~!!~ >> $option")

            if(option.name.isEmpty()) {
                Toast.makeText(context, R.string.opt_name_hint, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(position == -1)
                dialogListener.onOptAdd(option)
            else
                dialogListener.onOptSet(position, option)

            dismiss()
        }
        binding.delete.setOnClickListener {
            dialogListener.onItemDelete(position)
            dismiss()
        }
    }

    fun setOnDialogListener(dialogListener: DialogListener) {
        this.dialogListener = dialogListener
    }
}