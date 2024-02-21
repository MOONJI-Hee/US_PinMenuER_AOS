package com.wooriyo.pinmenuer.event.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wooriyo.pinmenuer.BaseDialogFragment
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogChoiceBinding

class EventConfirmDialog: BaseDialogFragment() {

    lateinit var binding: DialogChoiceBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogChoiceBinding.inflate(layoutInflater)

        binding.confirm.text = resources.getString(R.string.btn_save)

        binding.cancel.setOnClickListener{dismiss()}
        binding.confirm.setOnClickListener {

        }

        return binding.root
    }
}