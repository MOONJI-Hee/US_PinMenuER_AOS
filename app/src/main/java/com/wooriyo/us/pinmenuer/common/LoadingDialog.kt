package com.wooriyo.us.pinmenuer.common

import android.app.ActionBar.LayoutParams
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wooriyo.us.pinmenuer.BaseDialogFragment
import com.wooriyo.pinmenuer.databinding.DialogLoadingBinding

class LoadingDialog: BaseDialogFragment() {
    lateinit var binding: DialogLoadingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogLoadingBinding.inflate(layoutInflater)
        isCancelable = false
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window ?: return
        val params = window.attributes
        params.width = LayoutParams.MATCH_PARENT
        params.height = LayoutParams.MATCH_PARENT
        window.attributes = params
    }
}