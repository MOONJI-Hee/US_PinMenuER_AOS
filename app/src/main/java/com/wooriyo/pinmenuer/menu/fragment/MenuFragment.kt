package com.wooriyo.pinmenuer.menu.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.FragmentMenuBinding
import com.wooriyo.pinmenuer.menu.dialog.OptionDialog
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuFragment() : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
//
//    override fun getContext(): Context? {
//        return super.getContext()
//    }
//
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMenuBinding.inflate(inflater, container, false)

//        binding.etPrice.addTextChangedListener(textWatcher)
//
//        binding.menuSave.setOnClickListener{save()}
//        binding.optRequire.setOnClickListener(this)
//        binding.optChoice.setOnClickListener(this)

        return binding.root
    }
//
    override fun onClick(v: View?) {
//        when(v) {
//            binding.optRequire -> { OptionDialog(context, 1, null).show() }
//            binding.optChoice -> { OptionDialog(context, 2, null).show() }
//        }
    }
//
//    fun save() {
//        val mmtp = MediaType.parse("image/*") // 임시
//        var body : RequestBody?= null
//        var media1: MultipartBody.Part? = null
//        var media2: MultipartBody.Part? = null
//        var media3: MultipartBody.Part? = null
//
//        binding.run {
//            goods.name = etName.text.toString()
//            goods.content = etName.text.toString()
//            goods.cooking_time = (etCookingTime.text ?: "0").toString().toInt()
//            goods.price = etPrice.text.toString().replace(",", "").toInt()
//
//            if(goods.file1 != null) {
//                body = RequestBody.create(mmtp, goods.file1!!)
//                media1 = MultipartBody.Part.createFormData("img1", goods.file1!!.name, body)
//            }
//            if(goods.file2 != null) {
//                body = RequestBody.create(mmtp, goods.file2!!)
//                media2 = MultipartBody.Part.createFormData("img2", goods.file2!!.name, body)
//            }
//            if(goods.file3 != null) {
//                body = RequestBody.create(mmtp, goods.file3!!)
//                media3 = MultipartBody.Part.createFormData("img3", goods.file3!!.name, body)
//            }
//
//            goods.adDisplay = if(toggleSleep.isChecked) y else n
//
//            goods.icon = when {
//                rbNone.isChecked -> 1
//                rbHide.isChecked -> 2
//                rbBest.isChecked -> 3
//                rbSoldout.isChecked -> 4
//                rbNew.isChecked -> 5
//                else -> 0
//            }
//
//            goods.boption = if(toggleOption.isChecked) y else n
//        }
//
//        goods.let {
//            ApiClient.service.insGoods(useridx, storeidx, "001", it.name, it.content?:"", it.cooking_time, it.price, it.adDisplay, it.icon, it.boption)
//                .enqueue(object : Callback<ResultDTO> {
//                    override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
//                        Log.d(TAG, "메뉴 등록 url : $response")
//                        if(!response.isSuccessful) return
//
//                        val result = response.body()
//                        if(result != null) {
//                            when(result.status){
//                                1 -> {
//                                    Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
//                                    val gidx = result.gidx
//                                    uploadImage(gidx, media1, media2, media3)
//                                }
//                                else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//
//                    override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
//                        Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
//                        Log.d(TAG, "메뉴 등록 실패 > $t")
//                        Log.d(TAG, "메뉴 등록 실패 > ${call.request()}")
//                    }
//                })
//        }
//    }
//
//    fun uploadImage(gidx: Int, media1: MultipartBody.Part?, media2: MultipartBody.Part?, media3: MultipartBody.Part?) {
//        ApiClient.imgService.uploadImg(useridx, gidx, media1, media2, media3)
//            .enqueue(object : Callback<ResultDTO> {
//                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
//                    Log.d(TAG, "이미지 등록 url : $response")
//                    if(!response.isSuccessful) return
//
//                    val result = response.body()
//                    if(result != null) {
//                        when(result.status){
//                            1 -> { Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show() }
//                            else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
//                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
//                    Log.d(TAG, "이미지 등록 실패 > $t")
//                    Log.d(TAG, "이미지 등록 실패 > ${call.request()}")
//                }
//            })
//    }
//
//    val textWatcher = object : TextWatcher {
//        var result = ""
//        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            if(!s.isNullOrEmpty() && s.toString() != result) {
//                result = AppHelper.price(s.toString().replace(",", "").toInt())
//                binding.etPrice.setText(result)
//                binding.etPrice.setSelection(result.length)
//            }
//        }
//        override fun afterTextChanged(s: Editable?) {}
//    }
}