package com.wooriyo.pinmenuer.order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityOrderListBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.OrderHistoryDTO
import com.wooriyo.pinmenuer.model.OrderListDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.order.adapter.OrderAdapter
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask

class OrderListActivity : BaseActivity() {
    lateinit var binding: ActivityOrderListBinding
    lateinit var timer: Timer

    val TAG = "OrderListActivity"
    val mActivity = this@OrderListActivity

    val orderList = ArrayList<OrderHistoryDTO>()
    val orderAdapter = OrderAdapter(orderList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderAdapter.setOnCmpltClickListener(object:ItemClickListener{
            override fun onItemClick(position: Int) {setComplete(position)}
        })

        binding.rv.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rv.adapter = orderAdapter

        binding.back.setOnClickListener { finish() }

        getOrderList()
    }

    override fun onResume() {
        super.onResume()
        timer = Timer()
        val timerTask = object : TimerTask(){
            override fun run() {
                getOrdStatus()
            }
        }
        timer.schedule(timerTask, 0, 3000)
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    // 새로운 호출 유무 확인 > 3초마다 한번씩 태우기
    fun getOrdStatus() {
        ApiClient.service.getOrdStatus(useridx, storeidx).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "새로운 주문 유무 확인 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null && result.status == 1) {
                    getOrderList()
                    // 음악 재생
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "새로운 주문 유무 확인 실패 > $t")
                Log.d(TAG, "새로운 주문 유무 확인 실패 > ${call.request()}")
            }
        })
    }

    // 호출 확인 처리 > 화면 터치하면
    fun udtOrdStatus() {
        ApiClient.service.udtOrdStatus(useridx, storeidx).enqueue(object: Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "주문 확인 처리(상태 업데이트) url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null) {
                    when(result.status) {
                        1 -> {
                            // 알림음 종료 등등
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "주문 확인 처리(상태 업데이트) 실패 > $t")
                Log.d(TAG, "주문 확인 처리(상태 업데이트) 실패 > ${call.request()}")
            }
        })
    }

    // 주문 목록 조회
    fun getOrderList() {
        ApiClient.service.getOrderList(useridx, storeidx).enqueue(object : Callback<OrderListDTO>{
            override fun onResponse(call: Call<OrderListDTO>, response: Response<OrderListDTO>) {
                Log.d(TAG, "주문 목록 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null) {
                    when(result.status){
                        1 -> {
                            orderList.clear()
                            orderList.addAll(result.orderlist)
                            orderAdapter.notifyDataSetChanged()
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

    // 주문 완료 처리
    fun setComplete(position: Int) {

    }
}