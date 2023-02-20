package com.wooriyo.pinmenuer.store

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.call.CallListActivity
import com.wooriyo.pinmenuer.databinding.ActivityStoreMenuBinding
import com.wooriyo.pinmenuer.member.MemberSetActivity
import com.wooriyo.pinmenuer.menu.CategorySetActivity
import com.wooriyo.pinmenuer.menu.MenuSetActivity
import com.wooriyo.pinmenuer.model.CateListDTO
import com.wooriyo.pinmenuer.model.CategoryDTO
import com.wooriyo.pinmenuer.order.OrderListActivity
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import retrofit2.Call
import retrofit2.Response

class StoreMenuActivity : AppCompatActivity(), OnClickListener {
    lateinit var binding: ActivityStoreMenuBinding

    val TAG = "StoreMenuActivity"

    var useridx = 11
    var storeidx = 2
    var cateList = ArrayList<CategoryDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCategory()

        binding.run {
            back.setOnClickListener(this@StoreMenuActivity)
            udtMbr.setOnClickListener(this@StoreMenuActivity)
            order.setOnClickListener(this@StoreMenuActivity)
            call.setOnClickListener(this@StoreMenuActivity)
            menu.setOnClickListener(this@StoreMenuActivity)
            event.setOnClickListener(this@StoreMenuActivity)
            findMenu.setOnClickListener(this@StoreMenuActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.back -> startActivity(Intent(this@StoreMenuActivity, StoreListActivity::class.java))
            binding.udtMbr -> startActivity(Intent(this@StoreMenuActivity, MemberSetActivity::class.java))
            binding.order -> startActivity(Intent(this@StoreMenuActivity, OrderListActivity::class.java))
            binding.call -> startActivity(Intent(this@StoreMenuActivity, CallListActivity::class.java))
            binding.menu -> {
                if(cateList.isEmpty()) {
                    val intent = Intent(this@StoreMenuActivity, CategorySetActivity::class.java)
                    intent.putExtra("cateList", cateList)
                    startActivity(intent)
                }else {
                    val intent = Intent(this@StoreMenuActivity, MenuSetActivity::class.java)
                    intent.putExtra("cateList", cateList)
                    startActivity(intent)
                }
            }
            binding.event -> {}
            binding.findMenu -> {}
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
                            1 -> cateList.addAll(result.cateList)
                            else -> Toast.makeText(this@StoreMenuActivity, result.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<CateListDTO>, t: Throwable) {
                    Toast.makeText(this@StoreMenuActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "카테고리 조회 실패 > $t")
                }
            })
    }

}