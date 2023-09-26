package com.wooriyo.pinmenuer.common

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.databinding.DialogConfirmBinding

class ConfirmDialog(val content: String, val btn: String, val onClickListener: View.OnClickListener): DialogFragment() {
    lateinit var binding: DialogConfirmBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BaseDialog(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogConfirmBinding.inflate(layoutInflater)

        binding.content.text = content
        binding.confirm.text = btn

        binding.cancel.setOnClickListener { dismiss() }
        binding.confirm.setOnClickListener {
            onClickListener.onClick(it)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window ?: return
        val params = window.attributes
        params.width = 560
        params.height = 336
        window.attributes = params
    }
}