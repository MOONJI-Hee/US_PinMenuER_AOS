package com.wooriyo.pinmenuer.store

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.MyApplication.Companion.androidId
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityStoreListBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.member.MemberSetActivity
import com.wooriyo.pinmenuer.model.ResultDTO
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
    val mActivity = this

    var storeList = ArrayList<StoreDTO>()
    var storeAdapter = StoreAdapter(storeList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        useridx = MyApplication.pref.getUserIdx()

        storeAdapter.setOnItemClickListener(object : ItemClickListener {
            override fun onStoreClick(storeDTO: StoreDTO, intent: Intent) {
                checkDeviceLimit(storeDTO, intent)
            }
        })

        binding.rvStore.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvStore.adapter = storeAdapter

        binding.udtMbr.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        getStoreList()
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.udtMbr -> startActivity(Intent(mActivity, MemberSetActivity::class.java))
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
                            storeAdapter.notifyDataSetChanged()

                            if(storeList.size >= 4) {
                                binding.arrowLeft.visibility = View.VISIBLE
                                binding.arrowRight.visibility = View.VISIBLE
                            }
                        }else Toast.makeText(mActivity, storeListDTO.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<StoreListDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "매장 리스트 조회 오류 > $t")
                }
            })
    }

    fun checkDeviceLimit(store: StoreDTO, intent: Intent) {
        ApiClient.service.checkDeviceLimit(useridx, store.idx, androidId, MyApplication.pref.getToken().toString())
            .enqueue(object : retrofit2.Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "이용자수 체크 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    if(result.status == 1){
                        MyApplication.allCateList.clear()
                        MyApplication.storeidx = store.idx
                        MyApplication.store = store

                        startActivity(intent)
                    }else
                        Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "이용자수 체크 오류 > $t")
                    Log.d(TAG, "이용자수 체크 오류 > ${call.request()}")
                }
            })
    }
}