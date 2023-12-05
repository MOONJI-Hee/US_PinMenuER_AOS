package com.wooriyo.pinmenuer.setting

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityTablePassBinding
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TablePassActivity : BaseActivity() {
    lateinit var binding: ActivityTablePassBinding
    val mActivity = this@TablePassActivity
    val TAG = "TablePassActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTablePassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener { finish() }
        binding.save.setOnClickListener { save() }
    }

    fun save() {
        val pass = binding.etTablePass.text.toString()

        if(pass.isEmpty()) {
            Toast.makeText(mActivity, R.string.msg_no_pw, Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.service.udtTablePwd(MyApplication.useridx, MyApplication.storeidx, pass).enqueue(object :
            Callback<ResultDTO> {
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
}