package com.wooriyo.pinmenuer.menu

import android.content.Intent
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenuer.databinding.ActivityMenuSetBinding
import com.wooriyo.pinmenuer.menu.adpter.CateAdapter
import com.wooriyo.pinmenuer.menu.adpter.CateEditAdapter
import com.wooriyo.pinmenuer.menu.adpter.GoodsAdapter
import com.wooriyo.pinmenuer.menu.dialog.BgDialog
import com.wooriyo.pinmenuer.menu.dialog.OptionDialog
import com.wooriyo.pinmenuer.menu.dialog.ViewModeDialog
import com.wooriyo.pinmenuer.model.CategoryDTO
import com.wooriyo.pinmenuer.model.GoodsDTO
import com.wooriyo.pinmenuer.util.AppHelper

class MenuSetActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityMenuSetBinding
    lateinit var cateList: ArrayList<CategoryDTO>
    lateinit var cateAdapter : CateAdapter

    val goodsList = ArrayList<GoodsDTO>()
    val goodsAdapter = GoodsAdapter(goodsList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cateList = (intent.getSerializableExtra("cateList")?: ArrayList<CategoryDTO>()) as ArrayList<CategoryDTO>

        binding.back.setOnClickListener(this)
        binding.btnCateUdt.setOnClickListener(this)      // 카테고리 수정 버튼
        binding.setTablePass.setOnClickListener(this)   // 테이블 비밀번호 저장
        binding.setBg.setOnClickListener(this)              // 배경 선택
        binding.setViewMode.setOnClickListener(this)  // 뷰어 모드 선택
        binding.optRequire.setOnClickListener(this)
        binding.optChoice.setOnClickListener(this)

        setView()
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.back -> finish()
            binding.btnCateUdt -> {     // 카테고리 수정 버튼 > 카테고리 설정 페이지로 이동
                val intent = Intent(this@MenuSetActivity, CategorySetActivity::class.java)
                intent.putExtra("cateList", cateList)
                startActivity(intent)
            }
            binding.setTablePass -> {}
            binding.setBg -> { BgDialog(this@MenuSetActivity).show() }
            binding.setViewMode -> { ViewModeDialog(this@MenuSetActivity).show() }
            binding.optRequire -> { OptionDialog(this@MenuSetActivity, 1).show() }
            binding.optChoice -> { OptionDialog(this@MenuSetActivity, 2).show() }
        }
    }

    fun setView() {
        cateAdapter = CateAdapter(cateList)

        binding.run {
            rvCate.layoutManager = LinearLayoutManager(this@MenuSetActivity, LinearLayoutManager.HORIZONTAL, false)
            rvCate.adapter = cateAdapter

            rvMenu.layoutManager = LinearLayoutManager(this@MenuSetActivity, LinearLayoutManager.VERTICAL, false)
            rvMenu.adapter = goodsAdapter
        }
    }
}