package com.wooriyo.pinmenuer.menu.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.MyApplication.Companion.store
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogViewmodeBinding
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Response

class ViewModeDialog(context: Context): BaseDialog(context) {   // 메뉴판 뷰어모드 설정 다이얼로그
    lateinit var binding: DialogViewmodeBinding

    val TAG = "ViewModeDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogViewmodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when(store.viewmode) {
            "b" -> binding.checkBasic.isChecked = true
            "p" -> binding.check3x3.isChecked = true
        }

        binding.run {
            checkBasic.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) check3x3.isChecked = false
            }
            check3x3.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) checkBasic.isChecked = false
            }

            save.setOnClickListener { save() }
        }
    }

    fun save() {
        var selMode = ""

        when {
            binding.checkBasic.isChecked -> selMode = "b"
            binding.check3x3.isChecked -> selMode = "p"
        }

        ApiClient.service.setViewMode(useridx, store.idx, selMode).enqueue(object : retrofit2.Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "뷰어모드 설정 url > $response")
                if (!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        store.viewmode = selMode
                        Toast.makeText(context, R.string.complete, Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                    else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "뷰어모드 설정 실패 > $t")
                Log.d(TAG, "뷰어모드 설정 실패 > ${call.request()}")
            }
        })
    }
}