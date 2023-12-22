package com.wooriyo.pinmenuer.history

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.common.ConfirmDialog
import com.wooriyo.pinmenuer.databinding.ActivityByTableBinding
import com.wooriyo.pinmenuer.history.adapter.TableAdapter
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.OrderHistoryDTO
import com.wooriyo.pinmenuer.model.OrderListDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ByTableActivity : BaseActivity() {
    lateinit var binding: ActivityByTableBinding

    private val tableList = ArrayList<OrderHistoryDTO>()
    private val tableAdapter = TableAdapter(tableList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityByTableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAdapter()

        binding.rv.layoutManager = GridLayoutManager(mActivity, 4)
        binding.rv.adapter = tableAdapter

        binding.back.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        getTableList()
    }

    fun setAdapter() {
        tableAdapter.setOnCompleteClickListener(object: ItemClickListener{
            override fun onItemClick(position: Int) {
                val confirmDialog = ConfirmDialog(
                    "",
                    getString(R.string.dialog_table_complete_confirm),
                    getString(R.string.btn_complete)
                ) { completeTable(position) }

                confirmDialog.show(supportFragmentManager, "CompleteDialog")
            }
        })
    }

    private fun getTableList() {
        ApiClient.service.getTableHisList(MyApplication.useridx, MyApplication.storeidx)
            .enqueue(object: retrofit2.Callback<OrderListDTO>{
                override fun onResponse(call: Call<OrderListDTO>, response: Response<OrderListDTO>) {
                    Log.d(TAG, "테이블별 전체 내역 조회 url : $response")
                    if(!response.isSuccessful) return
                    val result = response.body() ?: return

                    if(result.status == 1) {
                        tableList.clear()
                        tableList.addAll(result.orderlist)
                        tableAdapter.notifyDataSetChanged()
                    }else
                        Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<OrderListDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "테이블별 전체 내역 조회 오류 >> $t")
                    Log.d(TAG, "테이블별 전체 내역 조회 오류 >> ${call.request()}")
                }
            })
    }

    private fun completeTable(position: Int) {
        ApiClient.service.setTableComplete(MyApplication.useridx, MyApplication.storeidx, tableList[position].tableNo, "Y")
            .enqueue(object : Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "테이블 전체 완료 처리 url : $response")
                    if(!response.isSuccessful) return
                    val result = response.body() ?: return

                    if(result.status == 1) {
                        tableList.removeAt(position)
                        tableAdapter.notifyItemRemoved(position)
                    }else
                        Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "테이블 전체 완료 처리 오류 >> $t")
                    Log.d(TAG, "테이블 전체 완료 처리 오류 >> ${call.request()}")
                }
            })
    }
}