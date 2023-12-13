package com.wooriyo.pinmenuer.menu.dialog

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogCategoryBinding
import com.wooriyo.pinmenuer.listener.DialogListener
import com.wooriyo.pinmenuer.model.CateListDTO
import com.wooriyo.pinmenuer.model.CategoryDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryDialog(context: Context, val position: Int, val category: CategoryDTO?): BaseDialog(context) {
    lateinit var binding : DialogCategoryBinding
    lateinit var dialogListener: DialogListener

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

        val params = window?.attributes
        params?.width = WindowManager.LayoutParams.WRAP_CONTENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = params
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        storeidx = MyApplication.storeidx

        if (position > -1 && category != null) {  // 수정
            binding.run {
                llUdt.visibility = View.VISIBLE
                save.visibility = View.GONE
                cateInfo.text = context.getString(R.string.category_dialog_info2)

                etName.setText(category.name)
                etExp.setText(category.subname)

                if(category.buse == n) cateUse.isChecked = true
            }
            cateidx = category.idx
            code = category.code
        }

        binding.run {
            close.setOnClickListener{dismiss()}
            save.setOnClickListener{save()}
            modify.setOnClickListener{modify()}
            delete.setOnClickListener{delete()}
        }
    }

    fun setOnDialogListener(dialogListener: DialogListener) {
        this.dialogListener = dialogListener
    }

    fun check(): Boolean {
        name = binding.etName.text.toString()
        subName = binding.etExp.text.toString()

        buse = if(binding.cateUse.isChecked) n else y           // 버튼은 '숨기기' & buse는 사용여부이기 떄문에 체크가 되어있을 때 '사용안함' = n

        return if(name.isEmpty()) {
            Toast.makeText(context, R.string.msg_no_name, Toast.LENGTH_SHORT).show()
            false
        }else { true }
    }

    fun save() {
        if(!check()) { return }

        ApiClient.service.insCate(useridx, storeidx, name, subName, buse)
            .enqueue(object: Callback<CateListDTO>{
                override fun onResponse(call: Call<CateListDTO>, response: Response<CateListDTO>) {
                    Log.d(TAG, "카테고리 등록 url : $response")
                    if(!response.isSuccessful) {return}
                    val resultDTO = response.body()
                    if(resultDTO != null) {
                        when(resultDTO.status) {
                            1 -> {
                                Toast.makeText(context, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                                dialogListener.onCateAdd(resultDTO.cateList)
                                dismiss()
                            }
                            else -> Toast.makeText(context, resultDTO.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<CateListDTO>, t: Throwable) {
                    Log.d(TAG, "카테고리 등록 실패 > $t")
                }
            })
    }

    private fun modify() {
        if(!check()) { return }

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
                                if(category != null) {
                                    category.name = name
                                    category.subname = subName
                                    category.buse = buse
                                    dialogListener.onCateSet(position, category)
                                }
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
                            dialogListener.onItemDelete(position)
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