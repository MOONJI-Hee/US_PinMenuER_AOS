package com.wooriyo.pinmenuer.pg

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityPgHistoryBinding
import com.wooriyo.pinmenuer.model.PgHistoryDTO
import com.wooriyo.pinmenuer.model.PgResultDTO
import com.wooriyo.pinmenuer.pg.adapter.PgHistoryAdapter
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PgHistoryActivity : BaseActivity() {
    lateinit var binding: ActivityPgHistoryBinding

    val pgHistoryList = ArrayList<PgHistoryDTO>()
    val pgHistoryAdapter = PgHistoryAdapter(pgHistoryList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPgHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvPgHistory.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvPgHistory.adapter = pgHistoryAdapter

        binding.back.setOnClickListener { finish() }
        binding.icSearch.setOnClickListener { search() }
    }

    override fun onResume() {
        super.onResume()
        getPgList()
    }

    fun search() {

    }

    fun getPgList() {
        ApiClient.service.getPgList(useridx, storeidx).enqueue(object: Callback<PgResultDTO>{
            override fun onResponse(call: Call<PgResultDTO>, response: Response<PgResultDTO>) {
                Log.d(TAG, "pg 결제내역 조회 url : $response")

                if(!response.isSuccessful) return

                val result = response.body() ?: return

                when(result.status) {
                    1 -> {
                        val removeList = ArrayList<PgHistoryDTO>()
                        result.pgHistoryList.forEach {
                            if(it.title.isEmpty())
                                removeList.add(it)
                        }
                        result.pgHistoryList.removeAll(removeList.toSet())

                        pgHistoryList.clear()
                        pgHistoryList.addAll(result.pgHistoryList)
                        pgHistoryAdapter.notifyDataSetChanged()
                    }
                    else -> {
                        Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<PgResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "pg 결제내역 조회 실패 > $t")
                Log.d(TAG, "pg 결제내역 조회 실패 > ${call.request()}")
            }
        })
    }
}