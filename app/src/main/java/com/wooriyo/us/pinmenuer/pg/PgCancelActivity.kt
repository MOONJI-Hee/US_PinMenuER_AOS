package com.wooriyo.us.pinmenuer.pg

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.us.pinmenuer.BaseActivity
import com.wooriyo.us.pinmenuer.MyApplication
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.databinding.ActivityPgCancelBinding
import com.wooriyo.us.pinmenuer.model.PgDetailDTO
import com.wooriyo.us.pinmenuer.model.PgDetailResultDTO
import com.wooriyo.us.pinmenuer.model.ResultDTO
import com.wooriyo.us.pinmenuer.pg.adapter.PgGoodsAdapter
import com.wooriyo.us.pinmenuer.pg.dialog.CancelDialog
import com.wooriyo.us.pinmenuer.util.ApiClient
import com.wooriyo.us.pinmenuer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PgCancelActivity : BaseActivity() {
    lateinit var binding: ActivityPgCancelBinding

    val goodsList = ArrayList<PgDetailDTO>()
    val goodsAdapter = PgGoodsAdapter(goodsList)

    var ordcode = ""
    var tid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPgCancelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ordcode = intent.getStringExtra("ordcode") ?: ""

        binding.rvGoods.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvGoods.adapter = goodsAdapter

        binding.back.setOnClickListener { finish() }
        binding.btnCancel.setOnClickListener {
            CancelDialog(mActivity) { pgCancel() }.show()
        }

        getPgDetail()
    }

    fun getPgDetail() {
        ApiClient.service.getPgDetail(MyApplication.useridx, MyApplication.storeidx, ordcode).enqueue(object: Callback<PgDetailResultDTO> {
            override fun onResponse(call: Call<PgDetailResultDTO>, response: Response<PgDetailResultDTO>) {
                Log.d(TAG, "pg 결제 내역 상세 조회 url : $response")

                if(!response.isSuccessful) return

                val result = response.body() ?: return

                when(result.status) {
                    1-> {
                        goodsList.clear()
                        goodsList.addAll(result.pgDetailList)
                        goodsAdapter.notifyDataSetChanged()

                        binding.run {
                            cardInfo.text = "${result.cardname} CARD NUM ${result.cardnum}"
                            price.text = AppHelper.price(result.amt)
                            regdt.text = result.pay_regdt
                            tableNo.text = result.tableNo
                        }

                        tid = result.tid
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<PgDetailResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "pg 결제 내역 상세 조회 오류 >> $t")
                Log.d(TAG, "pg 결제 내역 상세 조회 오류 >> ${call.request()}")
            }
        })
    }

    fun pgCancel() {
        ApiClient.service.cancelPG(MyApplication.useridx, MyApplication.storeidx, tid).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "결제취소 URL >> $response")
                if (!response.isSuccessful) return

                val result = response.body() ?: return
                when (result.status) {
                    1 -> {
                        Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "결제취소 오류 >> $t")
                Log.d(TAG, "결제취소 오류 >> ${call.request()}")
            }
        })
    }
}