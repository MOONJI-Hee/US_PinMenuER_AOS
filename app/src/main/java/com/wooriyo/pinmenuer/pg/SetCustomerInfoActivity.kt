package com.wooriyo.pinmenuer.pg

import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication.Companion.store
import com.wooriyo.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivitySetCustomerInfoBinding
import com.wooriyo.pinmenuer.model.PgResultDTO
import com.wooriyo.pinmenuer.model.PgTableDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.pg.adapter.PgTableAdapter
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetCustomerInfoActivity : BaseActivity() {
    lateinit var binding: ActivitySetCustomerInfoBinding
    val mActivity = this@SetCustomerInfoActivity
    val TAG = "SetCustomerInfoActivity"

    val tableList = ArrayList<PgTableDTO>()
    val tableAdapter = PgTableAdapter(tableList)

    var bisAll = true   // 테이블 전체 선택 여부
    var tb_cnt = 0      // 테이블 개수
    var sel_tb_cnt = 0  // 선택된 테이블 개수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetCustomerInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.run {
            useName.isChecked = store.blname == "Y"
            usePhone.isChecked = store.blphone == "Y"
            useAddr.isChecked = store.bladdr == "Y"
            useEtc.isChecked = store.bletc == "Y"
            useNoti.isChecked = store.blmemo == "Y"

            if(store.memo.isNotEmpty())
                memo.setText(store.memo)

            back.setOnClickListener { finish() }
            save.setOnClickListener { save() }
        }
    }

    fun save() {
        val useName = checkUse(binding.useName)
        val usePhone = checkUse(binding.usePhone)
        val useAddr = checkUse(binding.useAddr)
        val useEtc = checkUse(binding.useEtc)
        val useNoti = checkUse(binding.useNoti)
        val memo = binding.memo.text.toString()
        ApiClient.service.setQrCustomInfo(useridx, storeidx, useName, usePhone, useAddr, useEtc, useNoti, memo).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "결제고객 정보 설정 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        Toast.makeText(mActivity, R.string.complete, Toast.LENGTH_SHORT).show()
                        store.blname = useName
                        store.blphone = usePhone
                        store.bladdr = useAddr
                        store.bletc = useEtc
                        store.blmemo = useNoti
                        store.memo = memo
                        finish()
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "결제고객 정보 설정 오류 > $t")
                Log.d(TAG, "결제고객 정보 설정 오류 > ${call.request()}")
            }
        })
    }

    fun getTableList() {
        ApiClient.service.getTableList(useridx, storeidx).enqueue(object:Callback<PgResultDTO>{
            override fun onResponse(call: Call<PgResultDTO>, response: Response<PgResultDTO>) {
                Log.d(TAG, "테이블 리스트 조회 URL : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        tableList.clear()
                        tableList.addAll(result.tableList)
                        tb_cnt = tableList.size

                        bisAll = result.blAll == "Y"
                        binding.allTable.isChecked = bisAll
                        tableAdapter.checkAll(bisAll)
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<PgResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "테이블 리스트 조회 오류 > $t")
                Log.d(TAG, "테이블 리스트 조회 오류 > ${call.request()}")
            }
        })
    }

    private fun checkUse(v: CheckBox): String {
        return if(v.isChecked) "Y" else "N"
    }
}