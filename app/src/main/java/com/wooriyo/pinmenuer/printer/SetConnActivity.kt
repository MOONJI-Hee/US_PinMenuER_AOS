package com.wooriyo.pinmenuer.printer

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.MyApplication.Companion.androidId
import com.wooriyo.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivitySetConnBinding
import com.wooriyo.pinmenuer.listener.DialogListener
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.PrintContentDTO
import com.wooriyo.pinmenuer.model.PrintDTO
import com.wooriyo.pinmenuer.model.PrintListDTO
import com.wooriyo.pinmenuer.printer.adapter.PrinterAdapter
import com.wooriyo.pinmenuer.printer.dialog.SetNickDialog
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper.Companion.getPairedDevice
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetConnActivity : BaseActivity() {
    lateinit var binding: ActivitySetConnBinding

    val TAG = "SetConnActivity"
    val mActivity = this@SetConnActivity

    val printerList = ArrayList<PrintDTO>()
    val printerAdapter = PrinterAdapter(printerList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetConnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 디바이스, 프린터 정보 조회
        getPrintSetting()

        // 연결 프린터 리스트 조회 api
        getConnPrintList()

        // 페어링된 프린터 리스트 조회
        getPairedDevice()

        // 연결 프린트 리사이클러뷰
        printerAdapter.setConnClickListener(object : ItemClickListener {
            override fun onItemClick(position: Int) {
                val selType = printerList[position].printType

                //TODO selType으로 구분하여 프린트 연결
            }
        })

        binding.rvPrinter.layoutManager = LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false)
        binding.rvPrinter.adapter = printerAdapter

        val nickDialog = SetNickDialog(mActivity, "", 1, "안드로이드 태블릿 PC")
        nickDialog.setOnNickChangeListener(object : DialogListener {
            override fun onNickSet(nick: String) {
                binding.nickDevice.text = nick
            }
        })

        binding.back.setOnClickListener { finish() }
        binding.nickDevice.setOnClickListener { nickDialog.show() }
    }

    override fun onResume() {
        super.onResume()
    }

    fun getPrintSetting() {
        ApiClient.service.getPrintContentSet(useridx, MyApplication.storeidx, androidId).enqueue(object : Callback<PrintContentDTO>{
            override fun onResponse(call: Call<PrintContentDTO>, response: Response<PrintContentDTO>) {
                Log.d(TAG, "프린터 출력 내용 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        if(result.admnick.isNotEmpty())
                            binding.nickDevice.text = result.admnick
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PrintContentDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "프린터 출력 내용 조회 오류 >> $t")
                Log.d(TAG, "프린터 출력 내용 조회 오류 >> ${call.request()}")
            }
        })
    }

    fun getConnPrintList() {
        ApiClient.service.connPrintList(useridx, storeidx, androidId).enqueue(object : Callback<PrintListDTO> {
            override fun onResponse(call: Call<PrintListDTO>, response: Response<PrintListDTO>) {
                Log.d(TAG, "등록된 프린터 리스트 조회 URL : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return

                when(result.status) {
                    1 -> {
                        printerList.clear()
                        printerList.addAll(result.myprintList)
                        printerAdapter.notifyDataSetChanged()
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