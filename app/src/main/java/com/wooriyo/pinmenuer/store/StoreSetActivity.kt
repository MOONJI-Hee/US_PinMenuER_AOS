package com.wooriyo.pinmenuer.store

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.MyApplication.Companion.store
import com.wooriyo.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.common.MapActivity
import com.wooriyo.pinmenuer.databinding.ActivityStoreSetBinding
import com.wooriyo.pinmenuer.db.entity.Store
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.model.StoreListDTO
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoreSetActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding : ActivityStoreSetBinding

    val TAG = "StoreSetActivity"
    val mActivity = this@StoreSetActivity

    var type : Int = 0            // 1 : 등록, 2 : 수정

    //registerForActivityResult
    val setStore = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == RESULT_OK) {
            getStore()
        }
    }

    val setAddr = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == RESULT_OK) {
            store.address = it.data?.getStringExtra("address").toString()
            store.long = it.data?.getStringExtra("long").toString()
            store.lat = it.data?.getStringExtra("lat").toString()

            Log.d(TAG, "매장 주소 >> ${store.address}")
            Log.d(TAG, "매장 경도 >> ${store.long}")
            Log.d(TAG, "매장 위도 >> ${store.lat}")

            if(store.address.isNotEmpty())
                binding.etAddr.setText(store.address)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        useridx = MyApplication.pref.getUserIdx()

        type = intent.getIntExtra("type", type)
        if(type == 2) {
            binding.title.text = getString(R.string.title_udt_store)
            binding.save.visibility = View.GONE
            binding.llUdt.visibility = View.VISIBLE

            binding.etName.setText(store.name)
            binding.etAddr.setText(store.address)
        }else if(type == 1) {
//            binding.btnDetail.isEnabled = false
//            binding.btnHour.isEnabled = false
//            binding.btnImg.isEnabled = false
            binding.llDetail.visibility = View.GONE
            binding.llHour.visibility = View.GONE
            binding.llImg.visibility = View.GONE
        }

        binding.back.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.delete.setOnClickListener(this)
        binding.modify.setOnClickListener(this)
        binding.btnDetail.setOnClickListener(this)
        binding.btnHour.setOnClickListener(this)
        binding.btnImg.setOnClickListener(this)
        binding.btnMap.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.back -> finish()
            binding.save -> save()
            binding.delete -> delete()
            binding.modify -> modify()
            binding.btnDetail-> {
                setStore.launch(Intent(mActivity, StoreSetDetailActivity::class.java).putExtra("store", store))

//                if(type == 2)
//                    setStore.launch(Intent(mActivity, StoreSetDetailActivity::class.java).putExtra("store", store))
//                else if (type == 1)
//                    Toast.makeText(mActivity, R.string.msg_reg_store_first, Toast.LENGTH_SHORT).show()
            }
            binding.btnHour -> {
                setStore.launch(Intent(mActivity, StoreSetTimeActivity::class.java).putExtra("store", store))

//                if(type == 2)
//                    setStore.launch(Intent(mActivity, StoreSetTimeActivity::class.java).putExtra("store", store))
//                else if (type == 1)
//                    Toast.makeText(mActivity, R.string.msg_reg_store_first, Toast.LENGTH_SHORT).show()
            }
            binding.btnImg -> {
                setStore.launch(Intent(mActivity, StoreSetImgActivity::class.java).putExtra("store", store))

//                if(type == 2)
//                    setStore.launch(Intent(mActivity, StoreSetImgActivity::class.java).putExtra("store", store))
//                else if (type == 1)
//                    Toast.makeText(mActivity, R.string.msg_reg_store_first, Toast.LENGTH_SHORT).show()
            }
            binding.btnMap -> {
                val intent = Intent(mActivity, MapActivity::class.java)
                intent.putExtra("lat", store.lat) // 위도 (latitude)
                intent.putExtra("long", store.long) // 경도 (longitude)
                intent.putExtra("address", store.address)
                setAddr.launch(intent)
            }
        }
    }

    fun save() {
        store.name = binding.etName.text.toString()
        store.address = binding.etAddr.text.toString()

        if(store.name.isEmpty()) {
            Toast.makeText(mActivity, R.string.store_name_hint, Toast.LENGTH_SHORT).show()
        }else if (store.address.isEmpty()) {
            Toast.makeText(mActivity, R.string.msg_no_addr, Toast.LENGTH_SHORT).show()
        } else {
            ApiClient.service.regStore(useridx, store.name, store.address, store.long, store.lat)
                .enqueue(object : Callback<ResultDTO>{
                    override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                        Log.d(TAG, "매장 등록 url : $response")
                        val resultDTO = response.body()
                        if(resultDTO != null) {
                            if(resultDTO.status == 1) {
                                Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                                store.idx = resultDTO.idx
                                storeidx = resultDTO.idx
                                intent.putExtra("type", 2)
                                startActivity(intent)
                                finish()
                            }else {
                                Toast.makeText(mActivity, resultDTO.msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                        Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "매장 등록 실패 > $t")
                        Log.d(TAG, "매장 등록 실패 > ${call.request()}")
                    }
                })
        }
    }

    private fun delete() {
        ApiClient.service.delStore(useridx, storeidx)
            .enqueue(object : Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "매장 삭제 url : $response")
                    val resultDTO = response.body()
                    if(resultDTO != null) {
                        if(resultDTO.status == 1) {
                            Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            finish()
                        }else {
                            Toast.makeText(mActivity, resultDTO.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "매장 삭제 실패 > $t")
                    Log.d(TAG, "매장 삭제 실패 > ${call.request()}")
                }
            })
    }

    private fun modify() {
        store.name = binding.etName.text.toString()
        store.address = binding.etAddr.text.toString()
        if(store.name.isEmpty()) {
            Toast.makeText(this@StoreSetActivity, R.string.store_name_hint, Toast.LENGTH_SHORT).show()
        }else if (store.address.isEmpty()) {
            Toast.makeText(this@StoreSetActivity, R.string.msg_no_addr, Toast.LENGTH_SHORT).show()
        } else {
            ApiClient.service.udtStore(useridx, storeidx, store.name, store.address, store.long, store.lat)
                .enqueue(object : Callback<ResultDTO>{
                    override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                        Log.d(TAG, "매장 수정 url : $response")
                        val resultDTO = response.body()
                        if(resultDTO != null) {
                            if(resultDTO.status == 1) {
                                Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                                finish()
                            }else {
                                Toast.makeText(mActivity, resultDTO.msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                        Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "매장 수정 실패 > $t")
                        Log.d(TAG, "매장 수정 실패 > ${call.request()}")
                    }
                })
        }
    }

    fun getStore() {
        ApiClient.service.getStoreList(useridx, storeidx.toString())
            .enqueue(object: Callback<StoreListDTO>{
                override fun onResponse(call: Call<StoreListDTO>, response: Response<StoreListDTO>) {
                    Log.d(TAG, "한 매장 조회 url : $response")
                    if(!response.isSuccessful) return
                    val result = response.body()
                    if(result != null) {
                        when(result.status) {
                            1-> {
                                if(result.storeList.isNotEmpty()) {
                                    store = result.storeList[0]
                                    Log.d(TAG, "새로운 스토어~! >>> $store")
                                    binding.etName.setText(store.name)
                                    binding.etAddr.setText(store.address)
                                }
                            }
                            else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<StoreListDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "한 매장 조회 오류 > $t")
                }
            })
    }
}