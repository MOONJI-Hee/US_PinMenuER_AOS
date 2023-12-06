package com.wooriyo.pinmenuer

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.DialogFragment

open class BaseDialogFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BaseDialog(requireContext())
    }
    override fun onResume() {
        super.onResume()
        val window = dialog?.window ?: return
        val params = window.attributes
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = params
    }
}