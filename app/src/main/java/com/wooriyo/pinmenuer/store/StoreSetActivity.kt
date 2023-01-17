package com.wooriyo.pinmenuer.store

import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityStoreSetBinding
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoreSetActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding : ActivityStoreSetBinding

    val TAG = "StoreSetActivity"

    var type : Int = 0            // 1 : 등록, 2 : 수정
    var useridx : Int = 11
    var storeidx : Int = 0
    var storeNm : String = ""
    var storeZip : String = "" // 우편번호

    // 바깥화면 터치하면 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView = this.currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getIntExtra("type", type)
        if(type == 2) {
            storeidx = intent.getIntExtra("storeidx", storeidx)
            binding.save.visibility = View.GONE
            binding.llUdt.visibility = View.VISIBLE
        }

        //TODO useridx 받아오기
        binding.etAddr.setText( "전주시 완산구 홍산남로 75 6층")

        binding.back.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.delete.setOnClickListener(this)
        binding.modify.setOnClickListener(this)
        binding.btnDetail.setOnClickListener(this)
        binding.btnHour.setOnClickListener(this)
        binding.btnImg.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.back -> finish()
            binding.save -> save()
            binding.delete -> delete()
            binding.modify -> modify()
            binding.btnDetail -> startActivity(Intent(this@StoreSetActivity, StoreSetDetailActivity::class.java))
            binding.btnHour -> startActivity(Intent(this@StoreSetActivity, StoreSetHourActivity::class.java))
            binding.btnImg -> startActivity(Intent(this@StoreSetActivity, StoreSetImgActivity::class.java))
        }
    }

    fun save() {
        storeNm = binding.etName.text.toString()
        val storeAddr =binding.etAddr.text.toString()
        storeZip = "00000"

        if(storeNm.isEmpty()) {
            Toast.makeText(this@StoreSetActivity, R.string.store_name_hint, Toast.LENGTH_SHORT).show()
        }else if (storeAddr.isEmpty()) {
            Toast.makeText(this@StoreSetActivity, R.string.msg_no_addr, Toast.LENGTH_SHORT).show()
        } else {
            ApiClient.service.regStore(useridx, storeNm, storeAddr, storeZip)
                .enqueue(object : Callback<ResultDTO>{
                    override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                        Log.d(TAG, "매장 등록 / 수정 url : $response")
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
                        Log.d(TAG, "매장 등록 / 수정 실패 > $t")
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
                }
            })
    }

    private fun modify() {
        storeNm = binding.etName.text.toString()
        val storeAddr =binding.etAddr.text.toString()
        storeZip = "12345"

        if(storeNm.isEmpty()) {
            Toast.makeText(this@StoreSetActivity, R.string.store_name_hint, Toast.LENGTH_SHORT).show()
        }else if (storeAddr.isEmpty()) {
            Toast.makeText(this@StoreSetActivity, R.string.msg_no_addr, Toast.LENGTH_SHORT).show()
        } else {
            ApiClient.service.udtStore(useridx, storeidx, storeNm, storeAddr, storeZip)
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
                    }
                })
        }
    }
}