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
import com.wooriyo.pinmenuer.common.NoticeDialog
import com.wooriyo.pinmenuer.databinding.ActivityCallListBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.CallDTO
import com.wooriyo.pinmenuer.model.CallHistoryDTO
import com.wooriyo.pinmenuer.model.CallListDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.order.dialog.ClearDialog
import com.wooriyo.pinmenuer.store.StoreMenuActivity
import com.wooriyo.pinmenuer.util.Api
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CallListActivity : BaseActivity() {
    lateinit var binding: ActivityCallListBinding
    lateinit var clearDialog: ClearDialog
    lateinit var clearConfirmDialog: NoticeDialog
//    lateinit var timer: Timer

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

        setClearDialog()

        // 리사이클러뷰 초기화 및 어댑터 연결
        binding.rvCall.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCall.adapter = callListAdapter

        // 클릭 이벤트
        binding.back.setOnClickListener { finish() }
        binding.btnSet.setOnClickListener{ startActivity(Intent(mActivity, CallSetActivity::class.java)) }
        binding.icNew.setOnClickListener{
            getCallList()
            it.visibility = View.GONE
        }
        binding.btnClear.setOnClickListener { clearDialog.show() }

        getCallList()
    }

    override fun onResume() {
        super.onResume()
//        timer = Timer()
//        val timerTask: TimerTask = object : TimerTask() {
//            override fun run() {
//                getCallStatus()
//            }
//        }
//        timer.schedule(timerTask, 0, 3000)
    }

    // 초기화 / 초기화 확인 다이얼로그 초기화
    fun setClearDialog() {
        clearDialog = ClearDialog(
            mActivity,
            "call",
            View.OnClickListener {
                clearDialog.dismiss()
                clearConfirmDialog.show()
            })

        clearConfirmDialog = NoticeDialog(
            mActivity,
            getString(R.string.dialog_call_clear_title),
            getString(R.string.dialog_confrim_clear),
            View.OnClickListener { clear() }
        )
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