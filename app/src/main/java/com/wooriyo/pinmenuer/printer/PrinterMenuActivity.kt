package com.wooriyo.pinmenuer.printer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityPrinterMenuBinding
import com.wooriyo.pinmenuer.model.PrintDTO
import com.wooriyo.pinmenuer.model.PrintListDTO
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrinterMenuActivity : BaseActivity() {
    lateinit var binding: ActivityPrinterMenuBinding

    val TAG = "PrinterMenuActivity"
    val mActivity = this@PrinterMenuActivity

    val printerList = ArrayList<PrintDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrinterMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener{finish()}
        binding.setConn.setOnClickListener {
            if(printerList.isEmpty()) {
                startActivity(Intent(mActivity, NewConnActivity::class.java))
            }else {
                val intent = Intent(mActivity, SetConnActivity::class.java)
                intent.putExtra("printerList", printerList)
                startActivity(intent)
            }
        }
        binding.model.setOnClickListener { startActivity(Intent(mActivity, PrinterModelList::class.java)) }
        binding.setContents.setOnClickListener { startActivity(Intent(mActivity, SetContentActivity::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        getConnPrintList()
    }

    fun getConnPrintList() {
        ApiClient.service.connPrintList(useridx).enqueue(object : Callback<PrintListDTO> {
            override fun onResponse(call: Call<PrintListDTO>, response: Response<PrintListDTO>) {
                Log.d(TAG, "등록된 프린터 리스트 조회 URL : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return

                when(result.status) {
                    1 -> {
                        printerList.clear()
                        printerList.addAll(result.myprintList)
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PrintListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "등록된 프린터 리스트 조회 오류 >> $t")
                Log.d(TAG, "등록된 프린터 리스트 조회 오류 >> ${call.request()}")
            }
        })
    }
}