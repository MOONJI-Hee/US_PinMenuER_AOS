package com.wooriyo.pinmenuer.menu.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.MyApplication.Companion.store
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogBgBinding
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Response

class BgDialog(context: Context): BaseDialog(context) {
    lateinit var binding: DialogBgBinding

    val TAG = "BgDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogBgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.run{
            checkBlack.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    checkSilver.isChecked = false
                    checkLight.isChecked = false
                }
            }
            checkSilver.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    checkBlack.isChecked = false
                    checkLight.isChecked = false
                }
            }
            checkLight.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    checkBlack.isChecked = false
                    checkSilver.isChecked = false
                }
            }

//            save.setOnClickListener { save() }

            when(store.bgcolor) {
                "d" -> checkBlack.isChecked = true
                "g" -> checkSilver.isChecked = true
                "l" -> checkLight.isChecked = true
            }
        }
    }

    fun save() {
        var selColor = ""

        when {
            binding.checkBlack.isChecked -> selColor = "d"
            binding.checkSilver.isChecked -> selColor = "g"
            binding.checkLight.isChecked -> selColor = "l"
        }

        ApiClient.service.setBgColor(useridx, store.idx, selColor).enqueue(object : retrofit2.Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "배경 색 설정 url > $response")
                if (!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        store.bgcolor = selColor
                        Toast.makeText(context, R.string.complete, Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                    else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "배경 색 설정 실패 > $t")
                Log.d(TAG, "배경 색 설정 실패 > ${call.request()}")
            }
        })
    }
}