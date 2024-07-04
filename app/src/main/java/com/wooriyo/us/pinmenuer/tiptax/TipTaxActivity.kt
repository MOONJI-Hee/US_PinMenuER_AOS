package com.wooriyo.us.pinmenuer.tiptax

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.wooriyo.us.pinmenuer.BaseActivity
import com.wooriyo.us.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.us.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.databinding.ActivityTipTaxBinding
import com.wooriyo.us.pinmenuer.model.ResultDTO
import com.wooriyo.us.pinmenuer.model.TipTaxDTO
import com.wooriyo.us.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TipTaxActivity : BaseActivity() {
    lateinit var binding: ActivityTipTaxBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipTaxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getTipTax()

        binding.run {
            back.setOnClickListener { finish() }
            save.setOnClickListener { save() }
            useTip.setOnCheckedChangeListener { _, isChecked ->
                prop1.isEnabled = isChecked
                prop2.isEnabled = isChecked
                prop3.isEnabled = isChecked
                prop4.isEnabled = isChecked
            }
        }
    }

    private fun getTipTax() {
        ApiClient.service.getTipTax(useridx, storeidx).enqueue(object : Callback<TipTaxDTO> {
            override fun onResponse(call: Call<TipTaxDTO>, response: Response<TipTaxDTO>) {
                Log.d(TAG, "Tip & Tax 조회 url : $response")
                if(!response.isSuccessful) return
                val result = response.body() ?: return

                if(result.status == 1) {
                    binding.run {
                        useTip.isChecked = result.tipuse == "Y"
                        prop1.setText(result.tip1.toString())
                        prop2.setText(result.tip2.toString())
                        prop3.setText(result.tip3.toString())
                        prop4.setText(result.tip4.toString())
                        tax.setText(result.tax)
                    }
                }
            }
            override fun onFailure(call: Call<TipTaxDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Tip & Tax 조회 실패 >> $t")
                Log.d(TAG, "Tip & Tax 조회 실패 >> ${call.request()}")
            }
        })
    }

    fun save() {
        val tipUse = if(binding.useTip.isChecked) "Y" else "N"
        val tip1 = strToInt(binding.prop1)
        val tip2 = strToInt(binding.prop2)
        val tip3 = strToInt(binding.prop3)
        val tip4 = strToInt(binding.prop4)

        val tax = binding.tax.text.toString()

        ApiClient.service.setTipTax(useridx, storeidx, tipUse, tip1, tip2, tip3, tip4, tax).enqueue(object :
            Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "Tip & Tax 설정 url : $response")
                if(!response.isSuccessful) return
                val result = response.body() ?: return

                when(result.status) {
                    1 -> {
                        Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Tip & Tax 설정 실패 >> $t")
                Log.d(TAG, "Tip & Tax 설정 실패 >> ${call.request()}")
            }
        })
    }

    fun strToInt(v: EditText): Int {
        val str = v.text.toString()
        return if(str == "") 0 else str.toInt()
    }
}