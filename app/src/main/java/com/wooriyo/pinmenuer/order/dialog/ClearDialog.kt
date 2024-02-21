package com.wooriyo.pinmenuer.order.dialog

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.BaseDialogFragment
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogClearBinding

class ClearDialog(val callClickListener: OnClickListener, val ordClickListener: OnClickListener): BaseDialogFragment() {
    lateinit var binding: DialogClearBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogClearBinding.inflate(layoutInflater)

        binding.close.setOnClickListener { dismiss() }

        binding.clearCall.setOnClickListener {
            callClickListener.onClick(it)
            dismiss()
        }

        binding.clearOrder.setOnClickListener {
            ordClickListener.onClick(it)
            dismiss()
        }

        return binding.root
    }
}