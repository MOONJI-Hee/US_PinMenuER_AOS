package com.wooriyo.us.pinmenuer.printer

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.us.pinmenuer.BaseActivity
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.databinding.ActivityPrinterModelListBinding
import com.wooriyo.us.pinmenuer.model.PrintModelDTO
import com.wooriyo.us.pinmenuer.model.PrintModelListDTO
import com.wooriyo.us.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrinterModelListActivity : BaseActivity() {
    lateinit var binding: ActivityPrinterModelListBinding

    val modelList = ArrayList<PrintModelDTO>()

    var selpos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrinterModelListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getSupportPrinter()

        binding.back.setOnClickListener { finish() }
        binding.ts400b.setOnClickListener {
            binding.connWay.text = getText(R.string.printer_bluetooth)
//            binding.etc.text = getText(R.string.printer_etc_ts400b)
            binding.areaTs.setBackgroundResource(R.drawable.bg_r6w_b2_main)
            binding.areaCube.setBackgroundColor(Color.WHITE)
        }

        binding.sam4s.setOnClickListener {
            binding.connWay.text = getText(R.string.printer_wifi)
//            binding.etc.text = getText(R.string.printer_etc_gcube)
            binding.areaTs.setBackgroundColor(Color.WHITE)
            binding.areaCube.setBackgroundResource(R.drawable.bg_r6w_b2_main)
        }
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