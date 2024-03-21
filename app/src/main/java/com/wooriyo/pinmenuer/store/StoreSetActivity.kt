package com.wooriyo.pinmenuer.store

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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
            binding.etName2.setText(store.name2)
            binding.etAddr.setText(store.address)
        }else if(type == 1) {
            binding.btnDetail.isEnabled = false
            binding.btnHour.isEnabled = false
            binding.btnImg.isEnabled = false
//            binding.llDetail.visibility = View.GONE
//            binding.llHour.visibility = View.GONE
//            binding.llImg.visibility = View.GONE
        }

        setStoreInfo()

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

    fun setStoreInfo() {
        binding.run {
            if(store.img.isNotEmpty()) {
                tvStoreImg.visibility = View.GONE
                storeImg.visibility = View.VISIBLE
                Glide.with(mActivity)
                    .load(store.img)
                    .transform(CenterCrop(), RoundedCorners(6 * MyApplication.density.toInt()))
                    .into(storeImg)
            }else {
                tvStoreImg.visibility = View.VISIBLE
                storeImg.visibility = View.GONE
            }

            if(!store.content.isNullOrEmpty()) {
                storeExp.text = store.content
                storeExp.setTypeface(storeExp.typeface, Typeface.BOLD)
            }else {
                storeExp.text = getString(R.string.store_exp)
                storeExp.setTypeface(storeExp.typeface, Typeface.NORMAL)
            }

            if(!store.tel.isNullOrEmpty()) {
                storeTel.text = store.tel
                storeTel.typeface = Typeface.DEFAULT_BOLD
            }else {
                storeTel.text = getString(R.string.store_tel)
                storeTel.setTypeface(storeExp.typeface, Typeface.NORMAL)
            }

            if(!store.sns.isNullOrEmpty()) {
                storeSns.text = store.sns
                storeSns.typeface = Typeface.DEFAULT_BOLD
            }else {
                storeSns.text = getString(R.string.store_sns)
                storeSns.setTypeface(storeExp.typeface, Typeface.NORMAL)
            }

            if(store.delivery == "Y") {
                storeDeliver.text = getString(R.string.store_delivery_y)
                storeDeliver.typeface = Typeface.DEFAULT_BOLD
            }else {
                storeDeliver.text = getString(R.string.store_delivery_info)
                storeDeliver.setTypeface(storeExp.typeface, Typeface.NORMAL)
            }

            if(store.parking == "Y") {
                storePark.text = getString(R.string.store_parking_y)
                storePark.typeface = Typeface.DEFAULT_BOLD

                storeParkAdr.text = store.parkingAddr ?: ""
            }else {
                storePark.text = getString(R.string.store_parking_info)
                storePark.setTypeface(storeExp.typeface, Typeface.NORMAL)

                storeParkAdr.text = ""
            }
        }

        getTimeText()
    }

    fun getTimeText() {
        binding.run {
            if(store.opentime != null) {
                val time = store.opentime!!

                when(time.buse) {
                    y -> {
                        storeDay.text = getString(R.string.store_same_hour)
                        storeDay.typeface = Typeface.DEFAULT_BOLD

                        openTime.text = "${time.starttm}~${time.endtm}"
                        openTime.visibility = View.VISIBLE
                    }
                    n -> {
                        storeDay.text = getString(R.string.store_diff_hour)
                        storeDay.typeface = Typeface.DEFAULT_BOLD

                        var strOpen = ""
                        when(y) {
                            time.mon_buse -> {
                                strOpen += "/월 ${time.mon_starttm}~${time.mon_endtm}"
                            }
                            time.tue_buse -> {
                                strOpen += "/화 ${time.tue_starttm}~${time.tue_endtm}"
                            }
                            time.wed_buse -> {
                                strOpen += "/수 ${time.wed_starttm}~${time.wed_endtm}"
                            }
                            time.thu_buse -> {
                                strOpen += "/목 ${time.thu_starttm}~${time.thu_endtm}"
                            }
                            time.fri_buse -> {
                                strOpen += "/금 ${time.fri_starttm}~${time.fri_endtm}"
                            }
                            time.sat_buse -> {
                                strOpen += "/토 ${time.sat_starttm}~${time.sat_endtm}"
                            }
                            time.sun_buse -> {
                                strOpen += "/일 ${time.sun_starttm}~${time.sun_endtm}"
                            }
                        }

                        openTime.text = strOpen.drop(1)
                        openTime.visibility = View.VISIBLE
                    }
                    else -> { // D
                        storeDay.text = getString(R.string.store_day_of_week)
                        storeDay.setTypeface(storeExp.typeface, Typeface.NORMAL)
                        openTime.visibility = View.GONE
                    }
                }
            }
            if(store.breaktime != null) {
                val bTime = store.breaktime!!

                when(bTime.buse) {
                    y -> {
                        storeBreak.text = getString(R.string.store_same_break)
                        storeBreak.typeface = Typeface.DEFAULT_BOLD

                        breakTime.text = "${bTime.starttm}~${bTime.endtm}"
                        breakTime.visibility = View.VISIBLE
                    }
                    n -> {
                        storeBreak.text = getString(R.string.store_diff_break)
                        storeBreak.typeface = Typeface.DEFAULT_BOLD

                        var strBreak = ""
                        when(y) {
                            bTime.mon_buse -> {
                                strBreak += "/월 ${bTime.mon_starttm}~${bTime.mon_endtm}"
                            }
                            bTime.tue_buse -> {
                                strBreak += "/화 ${bTime.tue_starttm}~${bTime.tue_endtm}"
                            }
                            bTime.wed_buse -> {
                                strBreak += "/수 ${bTime.wed_starttm}~${bTime.wed_endtm}"
                            }
                            bTime.thu_buse -> {
                                strBreak += "/목 ${bTime.thu_starttm}~${bTime.thu_endtm}"
                            }
                            bTime.fri_buse -> {
                                strBreak += "/금 ${bTime.fri_starttm}~${bTime.fri_endtm}"
                            }
                            bTime.sat_buse -> {
                                strBreak += "/토 ${bTime.sat_starttm}~${bTime.sat_endtm}"
                            }
                            bTime.sun_buse -> {
                                strBreak += "/일 ${bTime.sun_starttm}~${bTime.sun_endtm}"
                            }
                        }
                        breakTime.text = strBreak.drop(1)
                        breakTime.visibility = View.VISIBLE
                    }
                    else -> { // D
                        storeBreak.text = getString(R.string.store_break)
                        storeBreak.setTypeface(storeBreak.typeface, Typeface.NORMAL)
                        breakTime.visibility = View.GONE
                    }
                }
            }
            when(store.hbuse) {
                y -> {
                    sameOff.visibility = View.VISIBLE
                    var strOff = ""
                    when(y) {
                        store.mon_buse -> strOff += "/월"
                        store.tue_buse -> strOff += "/화"
                        store.wed_buse -> strOff += "/수"
                        store.thu_buse -> strOff += "/목"
                        store.fri_buse -> strOff += "/금"
                        store.sat_buse -> strOff += "/토"
                        store.sun_buse -> strOff += "/일"
                    }
                    off.text = strOff.drop(1)
                    off.visibility = View.VISIBLE

                    storeOff.visibility = View.GONE
                }
                else -> {
                    sameOff.visibility =View.GONE
                    off.visibility = View.GONE
                }
            }

            if(store.spcHoliday != null) {
                val specHoliday = store.spcHoliday!!

                var strHoliday = ""
                specHoliday.forEach {
                    var hol = it.title
                    hol += if(it.month == "") { " 매월${it.day}일" }else { " ${it.month}월${it.day}일" }

                    strHoliday += "/$hol"
                }

                holiday.text = strHoliday.drop(1)
                holiday.visibility = View.VISIBLE
                storeHoliday.visibility = View.VISIBLE

                storeOff.visibility = View.GONE
            }else {
                holiday.visibility = View.GONE
                storeHoliday.visibility = View.GONE
            }
        }
    }

    fun save() {
        store.name = binding.etName.text.toString()
        store.name2 = binding.etName2.text.toString()
        store.address = binding.etAddr.text.toString()

        if(store.name.isEmpty()) {
            Toast.makeText(mActivity, R.string.store_name_hint, Toast.LENGTH_SHORT).show()
        }
//        else if (store.address.isEmpty()) {
//            Toast.makeText(mActivity, R.string.msg_no_addr, Toast.LENGTH_SHORT).show()
//        }
        else {
            ApiClient.service.regStore(useridx, store.name, store.name2, store.address, store.long, store.lat)
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
        store.name2 = binding.etName2.text.toString()
        store.address = binding.etAddr.text.toString()
        if(store.name.isEmpty()) {
            Toast.makeText(this@StoreSetActivity, R.string.store_name_hint, Toast.LENGTH_SHORT).show()
        }
//        else if (store.address.isEmpty()) {
//            Toast.makeText(this@StoreSetActivity, R.string.msg_no_addr, Toast.LENGTH_SHORT).show()
//        }
        else {
            ApiClient.service.udtStore(useridx, storeidx, store.name, store.name2, store.address, store.long, store.lat)
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

                                    setStoreInfo()
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