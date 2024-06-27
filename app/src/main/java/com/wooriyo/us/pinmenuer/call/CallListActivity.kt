package com.wooriyo.us.pinmenuer.call

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.us.pinmenuer.BaseActivity
import com.wooriyo.us.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.us.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.call.adapter.CallListAdapter
import com.wooriyo.us.pinmenuer.common.NoticeDialog
import com.wooriyo.us.pinmenuer.databinding.ActivityCallListBinding
import com.wooriyo.us.pinmenuer.listener.ItemClickListener
import com.wooriyo.us.pinmenuer.model.CallHistoryDTO
import com.wooriyo.us.pinmenuer.model.CallListDTO
import com.wooriyo.us.pinmenuer.model.ResultDTO
import com.wooriyo.us.pinmenuer.order.dialog.ClearDialog
import com.wooriyo.us.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class CallListActivity : BaseActivity() {
    lateinit var binding: ActivityCallListBinding
    lateinit var clearDialog: ClearDialog
    lateinit var clearConfirmDialog: NoticeDialog

    val callHistory = ArrayList<CallHistoryDTO>()
    val callListAdapter = CallListAdapter(callHistory)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 호출어댑터 리스너 설정 (완료 버튼 눌렀을 때 position 가져오기)
        callListAdapter.setOnItemClickListener(object : ItemClickListener {
            override fun onItemClick(position: Int) { setComplete(position) }
        })

        setClearDialog()

        // 리사이클러뷰 초기화 및 어댑터 연결
        binding.rvCall.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCall.adapter = callListAdapter

        // 클릭 이벤트
        binding.back.setOnClickListener { finish() }
        binding.btnSet.setOnClickListener{  }

        binding.btnClear.setOnClickListener { }

        getCallList()
    }

    override fun onResume() {
        super.onResume()
    }

    // 초기화 / 초기화 확인 다이얼로그 초기화
    fun setClearDialog() {
    }

    override fun onPause() {
        super.onPause()
//        timer.cancel()
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
                                binding.rvCall.visibility = View.GONE
                            }else {
                                binding.empty.visibility = View.GONE
                                binding.rvCall.visibility = View.VISIBLE
                                callListAdapter.notifyDataSetChanged()
//                                callHistory.sortBy { it.iscompleted }
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
//                        callHistory.sortBy { it.iscompleted }
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

    // 호출 초기화
    fun clear() {
        ApiClient.service.clearCall(useridx, storeidx).enqueue(object:Callback<ResultDTO>{
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