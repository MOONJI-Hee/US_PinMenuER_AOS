package com.wooriyo.pinmenuer.store

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityStoreListBinding
import com.wooriyo.pinmenuer.member.MemberSetActivity
import com.wooriyo.pinmenuer.model.StoreDTO
import com.wooriyo.pinmenuer.model.StoreListDTO
import com.wooriyo.pinmenuer.store.adpter.StoreAdapter
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import retrofit2.Call
import retrofit2.Response

class StoreListActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding : ActivityStoreListBinding

    val TAG = "StoreListActivity"
    val mActivity = this@StoreListActivity
    var useridx = 0

    var storeList = ArrayList<StoreDTO>()
    var storeAdapter = StoreAdapter(storeList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        useridx = MyApplication.pref.getUserIdx()

        binding.udtMbr.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        getStoreList()
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.udtMbr -> {
                startActivity(Intent(this@StoreListActivity, MemberSetActivity::class.java))
            }
        }
    }

    fun getStoreList() {
        ApiClient.service.getStoreList(useridx)
            .enqueue(object: retrofit2.Callback<StoreListDTO>{
                override fun onResponse(call: Call<StoreListDTO>, response: Response<StoreListDTO>) {
                    Log.d(TAG, "매장 리스트 조회 url : $response")
                    if(response.isSuccessful) {
                        val storeListDTO = response.body() ?: return
                        if(storeListDTO.status == 1) {
                            storeList.clear()
                            storeList.addAll(storeListDTO.storeList)
                            setStoreList()
                        }else {
                            Toast.makeText(this@StoreListActivity, storeListDTO.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<StoreListDTO>, t: Throwable) {
                    Toast.makeText(this@StoreListActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "매장 리스트 조회 오류 > $t")
                }
            })
    }

    fun setStoreList() {
        binding.rvStore.layoutManager = LinearLayoutManager(this@StoreListActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvStore.adapter = storeAdapter

        storeAdapter.notifyDataSetChanged()
    }
}