package com.wooriyo.pinmenuer.store.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogRegtimeBinding
import com.wooriyo.pinmenuer.listener.DialogListener
import com.wooriyo.pinmenuer.util.AppHelper
import com.wooriyo.pinmenuer.util.AppHelper.Companion.mkDouble

class RegTimeDialog(context: Context, var start: String, var end: String): Dialog(context) {
    lateinit var binding : DialogRegtimeBinding
    lateinit var dialogListener: DialogListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = DialogRegtimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(start != "00:00") {
            binding.etStartHour.setText(start.substring(0, 2))
            binding.etStartMin.setText(start.substring(3))
        }
        if(end != "00:00") {
            binding.etEndHour.setText(end.substring(0, 2))
            binding.etEndMin.setText(end.substring(3))
        }

        binding.close.setOnClickListener { dismiss() }
        binding.save.setOnClickListener { save() }
    }

    fun setOnTimeSetListener(dialogListener: DialogListener) {
        this.dialogListener = dialogListener
    }

    fun save() {
        val start_hour = binding.etStartHour.text ?: "00"
        val start_min = binding.etStartMin.text ?: "00"
        val end_hour = binding.etEndHour.text ?: "00"
        val end_min = binding.etEndMin.text ?: "00"

        start = "${mkDouble(start_hour.toString())}:${mkDouble(start_min.toString())}"
        end = "${mkDouble(end_hour.toString())}:${mkDouble(end_min.toString())}"

        if(start == "00:00" && end == "00:00") {
            Toast.makeText(context, R.string.msg_no_endtm, Toast.LENGTH_SHORT).show()
            return
        }

        dialogListener.onTimeSet(start, end)
    }

}