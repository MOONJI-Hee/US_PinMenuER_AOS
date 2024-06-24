package com.wooriyo.us.pinmenuer.event.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.wooriyo.us.pinmenuer.BaseDialogFragment
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogChoiceBinding

class EventConfirmDialog(val onClickListener: OnClickListener): BaseDialogFragment() {

    lateinit var binding: DialogChoiceBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogChoiceBinding.inflate(layoutInflater)

        binding.content.text = resources.getString(R.string.event_noti_switch_ff)
        binding.confirm.text = resources.getString(R.string.btn_save)

        binding.cancel.setOnClickListener{dismiss()}
        binding.confirm.setOnClickListener {
            onClickListener.onClick(it)
            dismiss()
        }

        return binding.root
    }
}