package com.wooriyo.pinmenuer.store

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.common.MapActivity
import com.wooriyo.pinmenuer.databinding.ActivityStoreSetBinding
import com.wooriyo.pinmenuer.db.entity.Store
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.model.StoreDTO
import com.wooriyo.pinmenuer.model.StoreListDTO
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoreSetActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding : ActivityStoreSetBinding

    val TAG = "StoreSetActivity"

    var type : Int = 0            // 1 : 등록, 2 : 수정
    var storeNm : String = ""
    var storeAddr : String = ""
    var storeLong : String = ""
    var storeLat : String = ""

    lateinit var store : StoreDTO

    //registerForActivityResult
    val setStore = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == RESULT_OK) {
            getStore()
        }
    }

    val setAddr = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == RESULT_OK) {
            storeAddr = it.data?.getStringExtra("address").toString()
            storeLong = it.data?.getStringExtra("long").toString()
            storeLat = it.data?.getStringExtra("lat").toString()

            Log.d(TAG, "매장 주소 >> $storeAddr")
            Log.d(TAG, "매장 경도 >> $storeLong")
            Log.d(TAG, "매장 위도 >> $storeLat")

            if(storeAddr.isNotEmpty())
                binding.etAddr.setText(storeAddr)
        }
    }

    // 바깥화면 터치하면 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        AppHelper.hideKeyboard(this, currentFocus, ev)
        return super.dispatchTouchEvent(ev)
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

            store = intent.getSerializableExtra("store") as StoreDTO
            storeidx = store.idx
            storeLat = store.lat
            storeLong = store.long

            binding.etName.setText(store.name)
            binding.etAddr.setText(store.address)
        }else if(type == 1) {
//            binding.btnDetail.isEnabled = false
//            binding.btnHour.isEnabled = false
//            binding.btnImg.isEnabled = false
            binding.llDetail.visibility = View.GONE
            binding.llHour.visibility = View.GONE
            binding.llImg.visibility = View.GONE

            store = StoreDTO(useridx)
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

    override fun onResume() {
        super.onResume()
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.back -> finish()
            binding.save -> save()
            binding.delete -> delete()
            binding.modify -> modify()
            binding.btnDetail-> {
                if(type == 2)
                    setStore.launch(Intent(this@StoreSetActivity, StoreSetDetailActivity::class.java).putExtra("store", store))
                else if (type == 1)
                    Toast.makeText(this@StoreSetActivity, R.string.msg_reg_store_first, Toast.LENGTH_SHORT).show()
            }
            binding.btnHour -> {
                if(type == 2)
                    setStore.launch(Intent(this@StoreSetActivity, StoreSetTimeActivity::class.java).putExtra("store", store))
                else if (type == 1)
                    Toast.makeText(this@StoreSetActivity, R.string.msg_reg_store_first, Toast.LENGTH_SHORT).show()
            }
            binding.btnImg -> {
                if(type == 2)
                    setStore.launch(Intent(this@StoreSetActivity, StoreSetImgActivity::class.java).putExtra("store", store))
                else if (type == 1)
                    Toast.makeText(this@StoreSetActivity, R.string.msg_reg_store_first, Toast.LENGTH_SHORT).show()
            }
            binding.btnMap -> {
                val intent = Intent(this@StoreSetActivity, MapActivity::class.java)
                intent.putExtra("lat", storeLat) // 위도 (latitude)
                intent.putExtra("long", storeLong) // 경도 (longitude)
                intent.putExtra("address", storeAddr)
                setAddr.launch(intent)
            }
        }
    }

    fun save() {
        storeNm = binding.etName.text.toString()
        storeAddr =binding.etAddr.text.toString()

        if(storeNm.isEmpty()) {
            Toast.makeText(this@StoreSetActivity, R.string.store_name_hint, Toast.LENGTH_SHORT).show()
        }else if (storeAddr.isEmpty()) {
            Toast.makeText(this@StoreSetActivity, R.string.msg_no_addr, Toast.LENGTH_SHORT).show()
        } else {
            ApiClient.service.regStore(useridx, storeNm, storeAddr, storeLong, storeLat)
                .enqueue(object : Callback<ResultDTO>{
                    override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                        Log.d(TAG, "매장 등록 url : $response")
                        val resultDTO = response.body()
                        if(resultDTO != null) {
                            if(resultDTO.status == 1) {
                                Toast.makeText(this@StoreSetActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                                intent.putExtra("type", 2)
                                finish()
                                startActivity(intent)
                            }else {
                                Toast.makeText(this@StoreSetActivity, resultDTO.msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                        Toast.makeText(this@StoreSetActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(this@StoreSetActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            finish()
                        }else {
                            Toast.makeText(this@StoreSetActivity, resultDTO.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(this@StoreSetActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "매장 삭제 실패 > $t")
                    Log.d(TAG, "매장 삭제 실패 > ${call.request()}")
                }
            })
    }

    private fun modify() {
        storeNm = binding.etName.text.toString()
        val storeAddr =binding.etAddr.text.toString()
        if(storeNm.isEmpty()) {
            Toast.makeText(this@StoreSetActivity, R.string.store_name_hint, Toast.LENGTH_SHORT).show()
        }else if (storeAddr.isEmpty()) {
            Toast.makeText(this@StoreSetActivity, R.string.msg_no_addr, Toast.LENGTH_SHORT).show()
        } else {
            ApiClient.service.udtStore(useridx, storeidx, storeNm, storeAddr, storeLong, storeLat)
                .enqueue(object : Callback<ResultDTO>{
                    override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                        Log.d(TAG, "매장 수정 url : $response")
                        val resultDTO = response.body()
                        if(resultDTO != null) {
                            if(resultDTO.status == 1) {
                                Toast.makeText(this@StoreSetActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                                finish()
                            }else {
                                Toast.makeText(this@StoreSetActivity, resultDTO.msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                        Toast.makeText(this@StoreSetActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
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
                            else -> Toast.makeText(this@StoreSetActivity, result.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<StoreListDTO>, t: Throwable) {
                    Toast.makeText(this@StoreSetActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "한 매장 조회 오류 > $t")
                }
            })
    }
}