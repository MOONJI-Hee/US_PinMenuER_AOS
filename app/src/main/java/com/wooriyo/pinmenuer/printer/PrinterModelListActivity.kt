package com.wooriyo.pinmenuer.printer

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityPrinterModelListBinding
import com.wooriyo.pinmenuer.model.PrintModelDTO
import com.wooriyo.pinmenuer.model.PrintModelListDTO
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrinterModelListActivity : BaseActivity() {
    lateinit var binding: ActivityPrinterModelListBinding

    val TAG = "PrintModelList"
    val mActivity = this@PrinterModelListActivity

    val modelList = ArrayList<PrintModelDTO>()

    var selpos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrinterModelListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getSupportPrinter()

        binding.back.setOnClickListener { finish() }
    }

    fun setView () {
        if(modelList.isNotEmpty()) {
            //TODO 서버에서 받아온 설명 보여주기
        }
    }

    fun getSupportPrinter() {
        ApiClient.service.getSupportList().enqueue(object : Callback<PrintModelListDTO> {
            override fun onResponse(call: Call<PrintModelListDTO>, response: Response<PrintModelListDTO>) {
                Log.d(TAG, "연결 가능 프린터 리스트 조회 URL : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        modelList.clear()
                        modelList.addAll(result.printList)
                        setView()
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PrintModelListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "연결 가능 프린터 리스트 조회 오류 >> $t")
                Log.d(TAG, "연결 가능 프린터 리스트 조회 오류 >> ${call.request()}")
            }
        })
    }
}