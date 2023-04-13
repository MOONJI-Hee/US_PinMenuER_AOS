package com.wooriyo.pinmenuer.call

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.call.adapter.CallSetAdapter
import com.wooriyo.pinmenuer.databinding.ActivityCallListBinding
import com.wooriyo.pinmenuer.listener.DialogListener
import com.wooriyo.pinmenuer.model.CallDTO
import com.wooriyo.pinmenuer.model.CallSetListDTO
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallSetActivity : BaseActivity(), DialogListener {
    val TAG = "CallSetActivity"
    val mActivity = this@CallSetActivity
    lateinit var binding: ActivityCallListBinding

    val setList = ArrayList<CallDTO>()
    private val callSetAdapter = CallSetAdapter(setList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // CallList와 레이아웃 같이 쓰기 때문에, SetActivity에 맞게 뷰 변경
        binding.btnSet.setBackgroundResource(R.drawable.bg_btn_r6_grd)
        binding.rvCall.visibility = View.GONE
        binding.callSetArea.visibility = View.VISIBLE

        binding.back.setOnClickListener{finish()}
        binding.btnSet.setOnClickListener{finish()}

        setView()
        getCallList()
    }

    override fun onCallSet(position: Int, data: CallDTO) {
        if(position == -1) {
            setList.add(data)
            data.gea = setList.size     // 여기서는 seq
            callSetAdapter.notifyItemInserted(setList.size-1)
        } else
            callSetAdapter.notifyItemChanged(position)
    }

    override fun onItemDelete(position: Int) {
        super.onItemDelete(position)
        setList.removeAt(position)
        callSetAdapter.notifyItemRemoved(position)
    }

    fun setView() {
        // 리사이클러뷰 초기화
        binding.rvCallSet.layoutManager = GridLayoutManager(mActivity, 4)
        binding.rvCallSet.adapter = callSetAdapter
    }

    private fun getCallList() {
        ApiClient.service.getCallList(useridx, storeidx)
            .enqueue(object : Callback<CallSetListDTO> {
                override fun onResponse(call: Call<CallSetListDTO>, response: Response<CallSetListDTO>) {
                    Log.d(TAG, "직원호출 전체 목록 조회 URL : $response")
                    if(!response.isSuccessful)
                        return

                    val callSetList = response.body()
                    if(callSetList != null) {
                        when(callSetList.status) {
                            1 -> {
                                setList.clear()
                                setList.addAll(callSetList.callList)
                                callSetAdapter.notifyDataSetChanged()
                            }
                            else -> Toast.makeText(mActivity, callSetList.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<CallSetListDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "직원호출 전체 목록 조회 오류 > $t")
                }
            })
    }
}