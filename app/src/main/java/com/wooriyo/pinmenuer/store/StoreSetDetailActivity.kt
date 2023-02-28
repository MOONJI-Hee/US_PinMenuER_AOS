package com.wooriyo.pinmenuer.store

import android.content.Intent
import android.os.Bundle
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
import com.wooriyo.pinmenuer.databinding.ActivityStoreSetDetailBinding
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.model.StoreDTO
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoreSetDetailActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding : ActivityStoreSetDetailBinding

    val TAG = "StoreSetDetailActivity"

    lateinit var store : StoreDTO

    var storeTel = ""
    var storeInsta = ""
    var delivery = ""
    var parking = ""
    var parkingAddr = ""
    var parkingLong = ""
    var parkingLat = ""

    val setAddr = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == RESULT_OK) {
            parkingAddr = it.data?.getStringExtra("address").toString()
            parkingLong = it.data?.getStringExtra("long").toString()
            parkingLat = it.data?.getStringExtra("lat").toString()
            if(parkingAddr.isNotEmpty())
                binding.etParkingAddr.setText(parkingAddr)
        }
    }

    // 바깥화면 터치하면 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        AppHelper.hideKeyboard(this, currentFocus, ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreSetDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        useridx = MyApplication.pref.getUserIdx()

        store = intent.getSerializableExtra("store") as StoreDTO
        storeidx = store.idx

        binding.run {
            if(store.tel != null)
                etTel.setText(store.tel)
            if(store.sns != null)
                etInsta.setText(store.sns)
            if(store.delivery != null) {
                when(store.delivery) {
                    y -> deliveryY.isChecked = true
                    n -> deliveryN.isChecked = true
                }
            }
            if(store.parking != null) {
                when(store.parking) {
                    y -> parkingY.isChecked = true
                    n -> parkingN.isChecked = true
                }
            }
            if(store.parkingAddr != null) {
                etParkingAddr.setText(store.parkingAddr)
                parkingLong = store.p_long.toString()
                parkingLat = store.p_lat.toString()
            }
        }

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
            binding.btnMap -> {
                val intent = Intent(this@StoreSetDetailActivity, MapActivity::class.java)
                intent.putExtra("lat", parkingLat)
                intent.putExtra("long", parkingLong)
                intent.putExtra("address", parkingAddr)
                setAddr.launch(intent)
            }
            binding.save -> save()
        }
    }

    private fun save() {
        storeTel = binding.etTel.text.toString()
        storeInsta = binding.etInsta.text.toString()
        parkingAddr = binding.etParkingAddr.text.toString()

        when(binding.groupDelivery.checkedRadioButtonId) {
            binding.deliveryY.id -> delivery = y
            binding.deliveryN.id -> delivery = n
        }

        when(binding.groupParking.checkedRadioButtonId) {
            binding.parkingY.id -> parking = y
            binding.parkingN.id -> parking = n
        }

        ApiClient.service.udtStoreDetail(useridx, storeidx, storeTel, storeInsta, delivery, parking, parkingAddr, parkingLong, parkingLat)
            .enqueue(object: Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "매장 상세정보 저장 url : $response")
                    val resultDTO = response.body()
                    if(resultDTO != null) {
                        if(resultDTO.status == 1) {
                            Toast.makeText(this@StoreSetDetailActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            setResult(RESULT_OK)
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