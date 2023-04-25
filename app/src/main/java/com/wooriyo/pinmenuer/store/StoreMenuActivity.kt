package com.wooriyo.pinmenuer.store

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.MyApplication.Companion.store
import com.wooriyo.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.MyApplication.Companion.allCateList
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.call.CallListActivity
import com.wooriyo.pinmenuer.databinding.ActivityStoreMenuBinding
import com.wooriyo.pinmenuer.member.MemberSetActivity
import com.wooriyo.pinmenuer.menu.CategorySetActivity
import com.wooriyo.pinmenuer.menu.MenuSetActivity
import com.wooriyo.pinmenuer.model.CateListDTO
import com.wooriyo.pinmenuer.model.CategoryDTO
import com.wooriyo.pinmenuer.model.StoreDTO
import com.wooriyo.pinmenuer.order.OrderListActivity
import com.wooriyo.pinmenuer.setting.MenuUiActivity
import com.wooriyo.pinmenuer.setting.TablePassActivity
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import retrofit2.Call
import retrofit2.Response

class StoreMenuActivity : BaseActivity(), OnClickListener {
    lateinit var binding: ActivityStoreMenuBinding

    val TAG = "StoreMenuActivity"
    val mActivity = this@StoreMenuActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCategory()

        binding.run {
            title.text = store.name

            back.setOnClickListener(mActivity)
            udtMbr.setOnClickListener(mActivity)
            order.setOnClickListener(mActivity)
            call.setOnClickListener(mActivity)
            menu.setOnClickListener(mActivity)
            tablePass.setOnClickListener(mActivity)
            menuUi.setOnClickListener(mActivity)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.back -> startActivity(Intent(mActivity, StoreListActivity::class.java))
            binding.udtMbr -> startActivity(Intent(mActivity, MemberSetActivity::class.java))
            binding.order -> startActivity(Intent(mActivity, OrderListActivity::class.java))
            binding.call -> startActivity(Intent(mActivity, CallListActivity::class.java))
            binding.menu -> {
                if(allCateList.isEmpty()) {
                    val intent = Intent(mActivity, CategorySetActivity::class.java)
                    startActivity(intent)
                }else {
                    val intent = Intent(mActivity, MenuSetActivity::class.java)
                    startActivity(intent)
                }
            }
            binding.tablePass -> startActivity(Intent(mActivity, TablePassActivity::class.java))
            binding.menuUi -> startActivity(Intent(mActivity, MenuUiActivity::class.java))
        }
    }

    fun getCategory() {
        ApiClient.service.getCateList(useridx, storeidx)
            .enqueue(object: retrofit2.Callback<CateListDTO>{
                override fun onResponse(call: Call<CateListDTO>, response: Response<CateListDTO>) {
                    Log.d(TAG, "카테고리 조회 url : $response")
                    if(!response.isSuccessful) {return}
                    val result = response.body()
                    if(result != null) {
                        when(result.status) {
                            1 -> allCateList.addAll(result.cateList)
                            else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<CateListDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "카테고리 조회 실패 > $t")
                    Log.d(TAG, "카테고리 조회 실패 > ${call.request()}")
                }
            })
    }

}