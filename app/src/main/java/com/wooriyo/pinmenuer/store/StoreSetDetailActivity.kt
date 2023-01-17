package com.wooriyo.pinmenuer.store

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityStoreSetDetailBinding
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoreSetDetailActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding : ActivityStoreSetDetailBinding

    val TAG = "StoreSetDetailActivity"

    var useridx = 11
    var storeidx = 2
    var storeTel = ""
    var storeInsta = ""
    var delivery = ""
    var parking = ""
    var parkingAddr = ""

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
        binding = ActivityStoreSetDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener(this)
        binding.btnMap.setOnClickListener(this)
        binding.save.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.back -> finish()
            binding.btnMap -> {}
            binding.save -> save()
        }
    }

    private fun save() {
        storeTel = binding.etTel.text.toString()
        storeInsta = binding.etInsta.text.toString()
        parkingAddr = binding.etParkingAddr.text.toString()

        when(binding.groupDelivery.checkedRadioButtonId) {
            binding.deliveryY.id -> delivery = "Y"
            binding.deliveryN.id -> delivery = "N"
        }

        when(binding.groupParking.checkedRadioButtonId) {
            binding.parkingY.id -> delivery = "Y"
            binding.parkingN.id -> delivery = "N"
        }

        ApiClient.service.udtStoreDetail(useridx, storeidx, storeTel, storeInsta, delivery, parking, parkingAddr)
            .enqueue(object: Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "매장 상세정보 저장 url : $response")
                    val resultDTO = response.body()
                    if(resultDTO != null) {
                        if(resultDTO.status == 1) {
                            Toast.makeText(this@StoreSetDetailActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            finish()
                        }else {
                            Toast.makeText(this@StoreSetDetailActivity, resultDTO.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(this@StoreSetDetailActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "매장 상세정보 저장 실패 > $t")
                }
            })
    }
}