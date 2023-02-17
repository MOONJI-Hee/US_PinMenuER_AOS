package com.wooriyo.pinmenuer.menu.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.wooriyo.pinmenuer.databinding.DialogViewmodeBinding

class ViewModeDialog(context: Context): Dialog(context) {   // 메뉴판 뷰어모드 설정 다이얼로그
    lateinit var binding: DialogViewmodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogViewmodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}