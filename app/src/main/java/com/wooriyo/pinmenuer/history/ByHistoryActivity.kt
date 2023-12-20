package com.wooriyo.pinmenuer.history

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.call.adapter.CallListAdapter
import com.wooriyo.pinmenuer.databinding.ActivityByHistoryBinding
import com.wooriyo.pinmenuer.history.adapter.HistoryAdapter
import com.wooriyo.pinmenuer.model.CallHistoryDTO
import com.wooriyo.pinmenuer.model.CallListDTO
import com.wooriyo.pinmenuer.model.OrderHistoryDTO
import com.wooriyo.pinmenuer.model.OrderListDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.order.adapter.OrderAdapter
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ByHistoryActivity : BaseActivity() {
    lateinit var binding: ActivityByHistoryBinding

    private val totalList = ArrayList<OrderHistoryDTO>()
    val totalAdapter = HistoryAdapter(totalList)

    private val orderList = ArrayList<OrderHistoryDTO>()
    val orderAdapter = OrderAdapter(orderList)

    private val callList = ArrayList<CallHistoryDTO>()
    val callAdapter = CallListAdapter(callList)

    private val completeList = ArrayList<OrderHistoryDTO>()
    val completeAdapter = HistoryAdapter(completeList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityByHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        getTotalList()
    }

    // 전체 목록 조회
    fun getTotalList() {
        ApiClient.service.getTotalList(MyApplication.useridx, MyApplication.storeidx).enqueue(object :
            Callback<OrderListDTO> {
            override fun onResponse(call: Call<OrderListDTO>, response: Response<OrderListDTO>) {
                Log.d(TAG, "주문 목록 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null) {
                    when(result.status){
                        1 -> {
                            totalList.clear()
                            totalList.addAll(result.orderlist)

                            if(totalList.isEmpty()) {
                                binding.empty.visibility = View.VISIBLE
                                binding.rv.visibility = View.GONE
                            }else {
                                binding.empty.visibility = View.GONE
                                binding.rv.visibility = View.VISIBLE

                            }
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<OrderListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "주문 목록 조회 오류 > $t")
                Log.d(TAG, "주문 목록 조회 오류 > ${call.request()}")
            }
        })
    }

    // 주문 목록 조회
    fun getOrderList() {

    }

    // 주문 초기화
    fun clearOrder() {
        ApiClient.service.clearOrder(MyApplication.useridx, MyApplication.storeidx).enqueue(object:
            Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "주문 초기화 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                if(result.status == 1){
                    getTotalList()
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "주문 초기화 실패 > $t")
                Log.d(TAG, "주문 초기화 실패 > ${call.request()}")
            }
        })
    }

    // 주문 완료 처리
    fun completeOrder(position: Int, isCompleted: Int, popup: Int) {
        val status = if(isCompleted == 1) "Y" else "N"
        ApiClient.service.udtComplete(MyApplication.storeidx, orderList[position].idx, status, popup)
            .enqueue(object: Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "주문 완료 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status){
                        1 -> {
                            Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            orderList[position].iscompleted = isCompleted
                            //                        orderList.sortBy { it.iscompleted }
                            orderAdapter.notifyItemChanged(position)
                            MyApplication.store.popup = popup
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "주문 완료 실패 > $t")
                    Log.d(TAG, "주문 완료 실패 > ${call.request()}")
                }
            })
    }

    // 주문 삭제
    fun deleteOrder(position: Int) {
        ApiClient.service.deleteOrder(MyApplication.storeidx, orderList[position].idx).enqueue(object:
            Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "주문 삭제 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status){
                    1 -> {
                        Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        orderList.removeAt(position)
                        orderAdapter.notifyItemRemoved(position)
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "주문 삭제 실패 > $t")
                Log.d(TAG, "주문 삭제 실패 > ${call.request()}")
            }
        })
    }

    // 호출 리스트 (히스토리) 조회
    fun getCallList() {
        ApiClient.service.getCallHistory(MyApplication.useridx, MyApplication.storeidx).enqueue(object:
            Callback<CallListDTO> {
            override fun onResponse(call: Call<CallListDTO>, response: Response<CallListDTO>) {
                Log.d(TAG, "호출 목록 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null) {
                    when(result.status){
                        1 -> {
                            callList.clear()
                            callList.addAll(result.callList)

                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<CallListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "호출 목록 조회 오류 > $t")
                Log.d(TAG, "호출 목록 조회 오류 > ${call.request()}")
            }
        })
    }

    // 호출 완료 처리
    fun completeCall(position: Int) {
        ApiClient.service.completeCall(MyApplication.storeidx, callList[position].idx, "Y").enqueue(object:
            Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "호출 완료 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status){
                    1 -> {
                        callList[position].iscompleted = 1
                        callAdapter.notifyItemChanged(position)
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "호출 완료 실패 > $t")
                Log.d(TAG, "호출 완료 실패 오류 > ${call.request()}")
            }
        })
    }

    // 호출 초기화
    fun clearCall() {
        ApiClient.service.clearCall(MyApplication.useridx, MyApplication.storeidx).enqueue(object:
            Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "직원호출 초기화 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                if(result.status == 1){
                    getCallList()
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "직원호출 초기화 실패 > $t")
                Log.d(TAG, "직원호출 초기화 실패 > ${call.request()}")
            }
        })
    }
}