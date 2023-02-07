package com.wooriyo.pinmenuer.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenuer.databinding.ActivityCategorySetBinding
import com.wooriyo.pinmenuer.menu.adpter.CateAdapter
import com.wooriyo.pinmenuer.menu.adpter.CateEditAdapter
import com.wooriyo.pinmenuer.menu.dialog.CategoryDialog
import com.wooriyo.pinmenuer.model.CategoryDTO
import com.wooriyo.pinmenuer.util.AppHelper

class CategorySetActivity : AppCompatActivity() {
    lateinit var binding : ActivityCategorySetBinding

    lateinit var allCateList : ArrayList<CategoryDTO>
    lateinit var cateAdapter : CateAdapter
    lateinit var cateEditAdapter : CateEditAdapter

    var flag = 0 // 순서 수정 모드 구분 > 0 : 수정모드 X, 1 : 수정모드 O

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategorySetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        allCateList = (intent.getSerializableExtra("cateList") ?: ArrayList<CategoryDTO>()) as ArrayList<CategoryDTO>
        if(allCateList.isNullOrEmpty()) {addDialog()}

        setView()

        binding.run {
            back.setOnClickListener { finish() }
            confirm.setOnClickListener {
                when(flag) {
                    0 -> startActivity(Intent(this@CategorySetActivity, MenuSetActivity::class.java))
                    1 -> seqSave()
                }
            }
            arrowLeft.setOnClickListener {  }
            arrowRight.setOnClickListener {  }
            btnCateAdd.setOnClickListener { addDialog() }
        }
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
    }

    fun setView() {
        cateAdapter = CateAdapter(allCateList)
        cateEditAdapter = CateEditAdapter(allCateList)

        binding.run {
            rvCate.layoutManager = LinearLayoutManager(this@CategorySetActivity, LinearLayoutManager.HORIZONTAL, false)
            rvCate.adapter = cateAdapter

            rvCateEdit.layoutManager = LinearLayoutManager(this@CategorySetActivity, LinearLayoutManager.HORIZONTAL, false)
            rvCateEdit.adapter = cateAdapter
        }
    }

    fun addDialog() {
        CategoryDialog(this@CategorySetActivity, 0).show()
    }

    fun seqSave() {

    }
}