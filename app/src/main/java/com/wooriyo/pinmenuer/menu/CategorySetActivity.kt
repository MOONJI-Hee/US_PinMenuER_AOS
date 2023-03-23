package com.wooriyo.pinmenuer.menu

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.databinding.ActivityCategorySetBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.menu.adpter.CateAdapter
import com.wooriyo.pinmenuer.menu.adpter.CateEditAdapter
import com.wooriyo.pinmenuer.menu.dialog.CategoryDialog
import com.wooriyo.pinmenuer.model.CategoryDTO
import com.wooriyo.pinmenuer.util.AppHelper

class CategorySetActivity : BaseActivity() {
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
        if(allCateList.isEmpty()) {addDialog()}

        setView()

        binding.run {
            back.setOnClickListener { finish() }
            confirm.setOnClickListener {
                intent = Intent(this@CategorySetActivity, MenuSetActivity::class.java)
                intent.putExtra("cateList", allCateList)
                startActivity(intent)
            }
            arrowLeft.setOnClickListener {  }
            arrowRight.setOnClickListener {  }
            btnAdd.setOnClickListener { addDialog() }
        }
    }

    fun setView() {
        cateAdapter = CateAdapter(allCateList, 0)
        cateEditAdapter = CateEditAdapter(allCateList)

        cateEditAdapter.setOnItemClickListener(object: ItemClickListener{
            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                CategoryDialog(this@CategorySetActivity, 1, allCateList[position]).show()
            }
        })

        cateEditAdapter.setOnMoveListener(object : ItemClickListener {
            override fun onItemMove(fromPos: Int, toPos: Int) {
                super.onItemMove(fromPos, toPos)
                cateEditAdapter.notifyItemMoved(fromPos, toPos)
            }
        })

        binding.run {
            rvCate.layoutManager = LinearLayoutManager(this@CategorySetActivity, LinearLayoutManager.HORIZONTAL, false)
            rvCate.adapter = cateAdapter

            rvCateEdit.layoutManager = LinearLayoutManager(this@CategorySetActivity, LinearLayoutManager.HORIZONTAL, false)
            rvCateEdit.adapter = cateEditAdapter
        }
    }

    fun addDialog() {
        CategoryDialog(this@CategorySetActivity, 0, null).show()
    }

    fun seqSave() {

    }
}