package com.wooriyo.pinmenuer.menu.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.DialogBgBinding

class BgDialog(context: Context): Dialog(context) {
    lateinit var binding: DialogBgBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogBgBinding.inflate(layoutInflater)
    }
}