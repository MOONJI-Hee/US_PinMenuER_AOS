package com.wooriyo.pinmenuer.store.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogRegtimeBinding
import com.wooriyo.pinmenuer.util.AppHelper

class RegTimeDialog(context: Context): Dialog(context) {
    lateinit var binding : DialogRegtimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = DialogRegtimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.close.setOnClickListener { dismiss() }
        binding.save.setOnClickListener { save() }
    }

    fun save() {
        val start_hour = binding.etStartHour.text ?: "00"
        val start_min = binding.etStartMin.text ?: "00"
        val end_hour = binding.etEndHour.text ?: "00"
        val end_min = binding.etEndMin.text ?: "00"

        val starttm = "${AppHelper.mkDouble(start_hour.toString())} : ${AppHelper.mkDouble(start_min.toString())}"
        val endtm = "${AppHelper.mkDouble(end_hour.toString())} : ${AppHelper.mkDouble(end_min.toString())}"

        // 끝나는 시간이 00:00일 때 리턴
        // 끝나는 시간이 시작시간보다 전일 때 리턴

        if(endtm == "00 : 00") {
            Toast.makeText(context, R.string.msg_no_endtm, Toast.LENGTH_SHORT).show()
            return
        }


    }

}