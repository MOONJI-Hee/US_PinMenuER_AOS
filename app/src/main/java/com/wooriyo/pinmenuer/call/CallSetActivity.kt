package com.wooriyo.pinmenuer.call

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.call.adapter.CallSetAdapter
import com.wooriyo.pinmenuer.databinding.ActivityCallListBinding
import com.wooriyo.pinmenuer.model.CallSetDTO
import com.wooriyo.pinmenuer.model.CallSetListDTO
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallSetActivity : AppCompatActivity() {
    val TAG = "CallSetActivity"
    lateinit var binding: ActivityCallListBinding

    var setList = ArrayList<CallSetDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // CallList와 레이아웃 같이 쓰기 때문에, SetActivity에 맞게 뷰 변경
        binding.btnSet.setBackgroundResource(R.drawable.bg_btn_r6_grd)
        binding.rvCall.visibility = View.GONE
        binding.callSetArea.visibility = View.VISIBLE

        getCallList()
    }

    private fun getCallList() {
        ApiClient.service.getCallList(MyApplication.pref.getUserIdx())
            .enqueue(object : Callback<CallSetListDTO> {
                override fun onResponse(call: Call<CallSetListDTO>, response: Response<CallSetListDTO>) {
                    Log.d(TAG, "직원호출 전체 목록 조회 URL : $response")
                    if(!response.isSuccessful)
                        return

                    val callSetList = response.body()
                    if(callSetList != null) {
                        when(callSetList.status) {
                            1 -> {
                                setList.addAll(callSetList.callList)
                                setView()
                            }
                            else -> Toast.makeText(this@CallSetActivity, callSetList.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<CallSetListDTO>, t: Throwable) {
                    Toast.makeText(this@CallSetActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "직원호출 전체 목록 조회 오류 > $t")
                }
            })
    }

    fun setView() {
        if(setList.isEmpty())
            setList.add(CallSetDTO())

        val callSetAdapter = CallSetAdapter(setList)
        // 리사이클러뷰 초기화
        binding.rvCallSet.layoutManager = GridLayoutManager(this@CallSetActivity, 4)
        binding.rvCallSet.adapter = callSetAdapter
    }
}