package com.wooriyo.pinmenuer.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenuer.databinding.ActivityMenuSetBinding
import com.wooriyo.pinmenuer.menu.adpter.CateAdapter
import com.wooriyo.pinmenuer.menu.adpter.CateEditAdapter
import com.wooriyo.pinmenuer.model.CategoryDTO
import com.wooriyo.pinmenuer.util.AppHelper

class MenuSetActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityMenuSetBinding
    lateinit var cateList: ArrayList<CategoryDTO>
    lateinit var cateAdapter : CateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cateList = (intent.getSerializableExtra("cateList")?: ArrayList<CategoryDTO>()) as ArrayList<CategoryDTO>

        binding.back.setOnClickListener(this)
        binding.btnCateUdt.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.back -> finish()
            binding.btnCateUdt -> {
                val intent = Intent(this@MenuSetActivity, CategorySetActivity::class.java)
                intent.putExtra("cateList", cateList)
                startActivity(intent)
            }
        }
    }

    fun setView() {
        cateAdapter = CateAdapter(cateList)

        binding.run {
            rvCate.layoutManager = LinearLayoutManager(this@MenuSetActivity, LinearLayoutManager.HORIZONTAL, false)
            rvCate.adapter = cateAdapter
        }
    }
}