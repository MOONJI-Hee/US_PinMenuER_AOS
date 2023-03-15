package com.wooriyo.pinmenuer.menu

import android.content.Intent
import android.graphics.Path
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityMenuSetBinding
import com.wooriyo.pinmenuer.menu.adpter.CateAdapter
import com.wooriyo.pinmenuer.menu.adpter.CateEditAdapter
import com.wooriyo.pinmenuer.menu.adpter.GoodsAdapter
import com.wooriyo.pinmenuer.menu.dialog.BgDialog
import com.wooriyo.pinmenuer.menu.dialog.OptionDialog
import com.wooriyo.pinmenuer.menu.dialog.ViewModeDialog
import com.wooriyo.pinmenuer.model.CategoryDTO
import com.wooriyo.pinmenuer.model.GoodsDTO
import com.wooriyo.pinmenuer.model.GoodsListDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.Api
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MenuSetActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityMenuSetBinding
    lateinit var cateList: ArrayList<CategoryDTO>
    lateinit var cateAdapter : CateAdapter

    val goodsList = ArrayList<GoodsDTO>()
    val goodsAdapter = GoodsAdapter(goodsList)

    val goods = GoodsDTO()

    val TAG = "MenuSetActivity"
    val mActivity = this@MenuSetActivity

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
        binding.menuSave.setOnClickListener{save()}
        binding.optRequire.setOnClickListener(this)
        binding.optChoice.setOnClickListener(this)

        setView()
        getMenu()
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.back -> finish()
            binding.btnCateUdt -> {     // 카테고리 수정 버튼 > 카테고리 설정 페이지로 이동
                val intent = Intent(mActivity, CategorySetActivity::class.java)
                intent.putExtra("cateList", cateList)
                startActivity(intent)
            }
            binding.setTablePass -> {setTablePass()}
            binding.setBg -> { BgDialog(mActivity).show() }
            binding.setViewMode -> { ViewModeDialog(mActivity).show() }
            binding.optRequire -> { OptionDialog(mActivity, 1, null).show() }
            binding.optChoice -> { OptionDialog(mActivity, 2, null).show() }
        }
    }

    fun setView() {
        cateAdapter = CateAdapter(cateList)

        binding.run {
            rvCate.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
            rvCate.adapter = cateAdapter

            rvMenu.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
            rvMenu.adapter = goodsAdapter

            etPrice.addTextChangedListener(textWatcher)
        }
    }

    fun getMenu() {
        ApiClient.service.getGoods(useridx, storeidx).enqueue(object : Callback<GoodsListDTO>{
            override fun onResponse(call: Call<GoodsListDTO>, response: Response<GoodsListDTO>) {
                Log.d(TAG, "메뉴 리스트 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null) {
                    when(result.status){
                        1 -> {
                            goodsList.clear()
                            goodsList.addAll(result.glist)
                            goodsAdapter.notifyDataSetChanged()
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<GoodsListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "메뉴 리스트 조회 실패 > $t")
            }
        })
    }

    fun save() {
        val mmtp = MediaType.parse("image/*") // 임시
        var body : RequestBody?= null
        var media1: MultipartBody.Part? = null
        var media2: MultipartBody.Part? = null
        var media3: MultipartBody.Part? = null

        binding.run {
            goods.name = etName.text.toString()
            goods.content = etName.text.toString()
            goods.cooking_time = (etCookingTime.text ?: "0").toString().toInt()
            goods.price = etPrice.text.toString().replace(",", "").toInt()

            if(goods.file1 != null) {
                body = RequestBody.create(mmtp, goods.file1!!)
                media1 = MultipartBody.Part.createFormData("img1", goods.file1!!.name, body)
            }
            if(goods.file2 != null) {
                body = RequestBody.create(mmtp, goods.file2!!)
                media2 = MultipartBody.Part.createFormData("img2", goods.file2!!.name, body)
            }
            if(goods.file3 != null) {
                body = RequestBody.create(mmtp, goods.file3!!)
                media3 = MultipartBody.Part.createFormData("img3", goods.file3!!.name, body)
            }

            goods.adDisplay = if(toggleSleep.isChecked) y else n

            goods.icon = when {
                rbNone.isChecked -> 1
                rbHide.isChecked -> 2
                rbBest.isChecked -> 3
                rbSoldout.isChecked -> 4
                rbNew.isChecked -> 5
                else -> 0
            }

            goods.boption = if(toggleOption.isChecked) y else n
        }

        goods.let {
            ApiClient.service.insGoods(useridx, storeidx, "001", it.name, it.content?:"", it.cooking_time, it.price, null, null, null, it.adDisplay, it.icon, it.boption)
//            ApiClient.service.insGoods(useridx, storeidx, "001", it.name, it.content?:"", it.cooking_time, it.price, it.adDisplay, it.icon, it.boption)
                .enqueue(object : Callback<ResultDTO> {
                    override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                        Log.d(TAG, "메뉴 등록 url : $response")
                        if(!response.isSuccessful) return

                        val result = response.body()

                        if(result != null) {
                            when(result.status){
                                1 -> Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                                else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                            }
                        }

                    }

                    override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                        Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "메뉴 등록 실패 > $t")
                        Log.d(TAG, "메뉴 등록 실패 > ${call.request()}")
                    }
                })
        }
    }

    fun setTablePass() {
        val pass = binding.etTablePass.text.toString()

        if(pass.isEmpty()) {
            Toast.makeText(mActivity, R.string.msg_no_pw, Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.service.udtTablePwd(useridx, storeidx, pass).enqueue(object : Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "테이블 비밀번호 설정 url : $response")
                if(!response.isSuccessful) return
                val result = response.body()
                if(result != null) {
                    when(result.status){
                        1 -> Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "테이블 비밀번호 설정 오류 > $t")
            }
        })
    }

    val textWatcher = object : TextWatcher {
        var result = ""
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(!s.isNullOrEmpty() && s.toString() != result) {
                result = AppHelper.price(s.toString().replace(",", "").toInt())
                binding.etPrice.setText(result)
                binding.etPrice.setSelection(result.length)
            }
        }
        override fun afterTextChanged(s: Editable?) {}
    }

}