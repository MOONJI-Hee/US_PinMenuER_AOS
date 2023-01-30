package com.wooriyo.pinmenuer.menu.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogCategoryBinding

class CategoryDialog(context: Context, val type: Int): Dialog(context) {
    lateinit var binding : DialogCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (type == 1) {  // 수정
            binding.llDsp.visibility = View.VISIBLE
            binding.llDsp.visibility = View.VISIBLE
            binding.save.visibility = View.INVISIBLE
            binding.cateInfo.text = context.getString(R.string.category_dialog_info2)
        }

        binding.run {
            close.setOnClickListener{dismiss()}
            save.setOnClickListener{save()}
            modify.setOnClickListener{modify()}
            delete.setOnClickListener{delete()}
        }
    }

    fun check() {

    }

    fun save() {

    }

    fun modify() {

    }

    fun delete() {

    }
}