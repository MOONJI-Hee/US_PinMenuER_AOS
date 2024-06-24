package com.wooriyo.us.pinmenuer.qrcode

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.us.pinmenuer.BaseActivity
import com.wooriyo.us.pinmenuer.MyApplication
import com.wooriyo.us.pinmenuer.MyApplication.Companion.store
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityPgAgreeBinding
import com.wooriyo.us.pinmenuer.model.ResultDTO
import com.wooriyo.us.pinmenuer.payment.SetPgInfoActivity
import com.wooriyo.us.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PgAgreeActivity : BaseActivity() {
    lateinit var binding: ActivityPgAgreeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPgAgreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(store.pg_snum.isNullOrEmpty() || store.pg_storenm.isNullOrEmpty()) {
            binding.niceStatus.text = "설정전"
        }else if (store.mid.isNullOrEmpty() || store.mid_key.isNullOrEmpty()) {
            binding.niceStatus.text = "심사중"
        }else {
            binding.niceStatus.text = "설정 완료"
        }

        binding.run {
            back.setOnClickListener { finish() }
            btnJudge.setOnClickListener { startActivity(Intent(mActivity, SetPgInfoActivity::class.java)) }
            btnQrSet.setOnClickListener { goQrSetting() }
        }
    }

    fun goQrSetting() {
        if(binding.checkAgree.isChecked)
            setNiceAgree()
        else
            Toast.makeText(mActivity, R.string.msg_do_agree, Toast.LENGTH_SHORT).show()
    }

    fun setNiceAgree() {
        ApiClient.service.setNiceAgree(MyApplication.storeidx, MyApplication.androidId).enqueue(object: Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "이행보증보험 동의 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> startActivity(Intent(mActivity, SetQrcodeActivity::class.java))
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "이행보증보험 동의 오류 >> $t")
                Log.d(TAG, "이행보증보험 동의 오류 >> ${call.request()}")
            }
        })
    }
}