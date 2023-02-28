package com.wooriyo.pinmenuer.menu.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogCategoryBinding
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryDialog(context: Context, val type: Int): BaseDialog(context) {
    lateinit var binding : DialogCategoryBinding

    val TAG = "CategoryDialog"
    var storeidx = 0
    var cateidx = 0
    var code = ""
    var name = ""
    var subName = ""
    var buse = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (type == 1) {  // 수정
            binding.llUse.visibility = View.VISIBLE
            binding.llUdt.visibility = View.VISIBLE
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

    fun check(): Boolean {
        name = binding.etName.text.toString()
        subName = binding.etExp.text.toString()

        buse = if(binding.cateUse.isChecked) "Y" else "N"

        return if(name.isEmpty()) {
            Toast.makeText(context, R.string.msg_no_name, Toast.LENGTH_SHORT).show()
            false
        }else { true }
    }

    fun save() {
        if(!check()) { return }

        ApiClient.service.insCate(useridx, storeidx, name, subName, buse)
            .enqueue(object: Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "카테고리 등록 url : $response")
                    if(!response.isSuccessful) {return}
                    val resultDTO = response.body()
                    if(resultDTO != null) {
                        when(resultDTO.status) {
                            1 -> {
                                Toast.makeText(context, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                            else -> Toast.makeText(context, resultDTO.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Log.d(TAG, "카테고리 등록 실패 > $t")
                }
            })
    }

    private fun modify() {
        ApiClient.service.udtCate(useridx, storeidx, cateidx, name, subName, buse)
            .enqueue(object: Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "카테고리 수정 url : $response")
                    if(!response.isSuccessful) {return}
                    val resultDTO = response.body()
                    if(resultDTO != null) {
                        when(resultDTO.status) {
                            1 -> {
                                Toast.makeText(context, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                            else -> Toast.makeText(context, resultDTO.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Log.d(TAG, "카테고리 수정 실패 > $t")
                }
            })
    }

    fun delete() {
        ApiClient.service.delCate(useridx, storeidx, cateidx, code).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "카테고리 삭제 url : $response")
                if(!response.isSuccessful) {return}
                val resultDTO = response.body()
                if(resultDTO != null) {
                    when(resultDTO.status) {
                        1 -> {
                            Toast.makeText(context, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                        else -> Toast.makeText(context, resultDTO.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Log.d(TAG, "카테고리 삭제 실패 > $t")
            }
        })
    }
}