package com.wooriyo.pinmenuer.call

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.call.adapter.CallListAdapter
import com.wooriyo.pinmenuer.databinding.ActivityCallListBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.CallDTO
import com.wooriyo.pinmenuer.model.CallHistoryDTO
import com.wooriyo.pinmenuer.model.CallListDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.store.StoreMenuActivity
import com.wooriyo.pinmenuer.util.Api
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CallListActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityCallListBinding
    lateinit var timer: Timer

    val TAG = "CallListActivity"
    val mActivity = this@CallListActivity

    val callHistory = ArrayList<CallHistoryDTO>()
    val callListAdapter = CallListAdapter(callHistory)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 호출어댑터 리스너 설정 (완료 버튼 눌렀을 때 position 가져오기)
        callListAdapter.setOnItemClickListener(object : ItemClickListener{
            override fun onItemClick(position: Int) { setComplete(position) }
        })
        // 리사이클러뷰 초기화 및 어댑터 연결
        binding.rvCall.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCall.adapter = callListAdapter

        // 클릭 이벤트
        binding.back.setOnClickListener(this)
        binding.btnSet.setOnClickListener(this)

        getCallList()
    }

    override fun onResume() {
        super.onResume()
        timer = Timer()
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                getCallStatus()
            }
        }
        timer.schedule(timerTask, 0, 3000)
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.back -> finish()
            binding.btnSet -> startActivity(Intent(mActivity, CallSetActivity::class.java))
        }
    }

    // 새로운 호출 유무 확인 > 3초마다 한번씩 태우기
    fun getCallStatus() {
        ApiClient.service.getCallStatus(useridx, storeidx).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "새로운 호출 유무 확인 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null && result.status == 1) {
                    getCallList()
                    // 음악 재생
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "새로운 호출 유무 확인 실패 > $t")
                Log.d(TAG, "새로운 호출 유무 확인 실패 > ${call.request()}")
            }
        })
    }

    // 호출 확인 처리 > 화면 터치하면
    fun udtCallStatus() {
        ApiClient.service.udtCallStatus(useridx, storeidx).enqueue(object: Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "호출 확인 처리(상태 업데이트) url : $response")
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
                Log.d(TAG, "호출 확인 처리(상태 업데이트) 실패 > $t")
                Log.d(TAG, "호출 확인 처리(상태 업데이트) 실패 > ${call.request()}")
            }
        })
    }

    // 호출 리스트 (히스토리) 조회
    fun getCallList() {
        ApiClient.service.getCallHistory(useridx, storeidx).enqueue(object: Callback<CallListDTO>{
            override fun onResponse(call: Call<CallListDTO>, response: Response<CallListDTO>) {
                Log.d(TAG, "호출 목록 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null) {
                    when(result.status){
                        1 -> {
                            callHistory.clear()
                            callHistory.addAll(result.callList)

                            if(callHistory.isEmpty()) {
                                binding.empty.visibility = View.VISIBLE
                            }else {
                                binding.empty.visibility = View.GONE
                                callHistory.sortBy { it.iscompleted }
                                callListAdapter.notifyDataSetChanged()
                            }
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
    fun setComplete(position: Int) {
        ApiClient.service.completeCall(storeidx, callHistory[position].idx, "Y").enqueue(object:Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "호출 완료 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status){
                    1 -> {
                        callHistory[position].iscompleted = 1
                        callHistory.sortBy { it.iscompleted }
                        callListAdapter.notifyItemChanged(position)
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
}