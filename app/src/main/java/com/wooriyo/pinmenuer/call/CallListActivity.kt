package com.wooriyo.pinmenuer.call

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.call.adapter.CallListAdapter
import com.wooriyo.pinmenuer.databinding.ActivityCallListBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.CallDTO
import com.wooriyo.pinmenuer.model.CallListDTO
import com.wooriyo.pinmenuer.store.StoreMenuActivity
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Callback

class CallListActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityCallListBinding

    val TAG = "CallListActivity"
    val mActivity = this@CallListActivity

    var callList = ArrayList<CallListDTO>()
    var callListAdapter = CallListAdapter(callList)

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

        binding.back.setOnClickListener(this)
        binding.btnSet.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.back -> startActivity(Intent(mActivity, StoreMenuActivity::class.java))
            binding.btnSet -> startActivity(Intent(mActivity, CallSetActivity::class.java))
        }
    }

    fun getCallList() {
    }

    // 호출 완료 처리
    fun setComplete(position: Int) {

    }
}