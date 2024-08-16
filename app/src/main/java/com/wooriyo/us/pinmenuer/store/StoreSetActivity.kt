package com.wooriyo.us.pinmenuer.store

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wooriyo.us.pinmenuer.BaseActivity
import com.wooriyo.us.pinmenuer.MyApplication
import com.wooriyo.us.pinmenuer.MyApplication.Companion.store
import com.wooriyo.us.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.us.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.databinding.ActivityStoreSetBinding
import com.wooriyo.us.pinmenuer.model.ResultDTO
import com.wooriyo.us.pinmenuer.model.StoreListDTO
import com.wooriyo.us.pinmenuer.util.ApiClient
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
            binding.etName2.setText(store.subname)
            binding.etAddr.setText(store.address)
        }else if(type == 1) {
            binding.btnDetail.isEnabled = false
            binding.btnHour.isEnabled = false
            binding.btnImg.isEnabled = false
        }

        setStoreInfo()

        binding.back.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.delete.setOnClickListener(this)
        binding.modify.setOnClickListener(this)
        binding.btnDetail.setOnClickListener(this)
        binding.btnHour.setOnClickListener(this)
        binding.btnImg.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.back -> finish()
            binding.save -> save()
            binding.delete -> delete()
            binding.modify -> modify()
            binding.btnDetail-> {
                setStore.launch(Intent(mActivity, StoreSetDetailActivity::class.java).putExtra("store", store))
            }
            binding.btnHour -> {
                setStore.launch(Intent(mActivity, StoreSetTimeActivity::class.java).putExtra("store", store))
            }
            binding.btnImg -> {
                setStore.launch(Intent(mActivity, StoreSetImgActivity::class.java).putExtra("store", store))
            }
        }
    }

    fun setStoreInfo() {
        binding.run {
            if(store.img.isNotEmpty()) {
                storeImg.visibility = View.VISIBLE
                Glide.with(mActivity)
                    .load(store.img)
                    .transform(CenterCrop(), RoundedCorners(6 * MyApplication.density.toInt()))
                    .into(storeImg)
            }else {
                storeImg.visibility = View.GONE
            }

            if(!store.content.isNullOrEmpty()) {
                storeExp.visibility = View.VISIBLE
                storeExp.text = store.content
            }else {
                storeExp.visibility = View.GONE
            }

            if(!store.tel.isNullOrEmpty()) {
                storeTel.visibility = View.VISIBLE
                storeTel.text = store.tel
            }else {
                storeTel.visibility = View.GONE
            }

            if(!store.sns.isNullOrEmpty()) {
                storeSns.visibility = View.VISIBLE
                storeSns.text = store.sns
            }else {
                storeSns.visibility = View.GONE
            }

            if(store.parking == "Y") {
                storePark.visibility = View.VISIBLE
                storePark.text = getString(R.string.store_parking_y)
                storeParkAdr.text = store.parkingAddr ?: ""
            }else {
                storePark.visibility = View.GONE
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
                        storeDay.visibility = View.VISIBLE

                        openTime.text = "${time.starttm}~${time.endtm}"
                        openTime.visibility = View.VISIBLE
                    }
                    n -> {
                        storeDay.text = getString(R.string.store_diff_hour)
                        storeDay.visibility = View.VISIBLE

                        var strOpen = ""
                        when(y) {
                            time.mon_buse -> {
                                strOpen += "/MO ${time.mon_starttm}~${time.mon_endtm}"
                            }
                            time.tue_buse -> {
                                strOpen += "/TU ${time.tue_starttm}~${time.tue_endtm}"
                            }
                            time.wed_buse -> {
                                strOpen += "/WE ${time.wed_starttm}~${time.wed_endtm}"
                            }
                            time.thu_buse -> {
                                strOpen += "/TH ${time.thu_starttm}~${time.thu_endtm}"
                            }
                            time.fri_buse -> {
                                strOpen += "/FR ${time.fri_starttm}~${time.fri_endtm}"
                            }
                            time.sat_buse -> {
                                strOpen += "/SA ${time.sat_starttm}~${time.sat_endtm}"
                            }
                            time.sun_buse -> {
                                strOpen += "/SU ${time.sun_starttm}~${time.sun_endtm}"
                            }
                        }

                        openTime.text = strOpen.drop(1)
                        openTime.visibility = View.VISIBLE
                    }
                    else -> { // D
                        storeDay.visibility = View.GONE
                        openTime.visibility = View.GONE
                    }
                }
            }
            if(store.breaktime != null) {
                val bTime = store.breaktime!!

                when(bTime.buse) {
                    y -> {
                        storeBreak.text = getString(R.string.store_same_break)
                        storeBreak.visibility = View.VISIBLE

                        breakTime.text = "${bTime.starttm}~${bTime.endtm}"
                        breakTime.visibility = View.VISIBLE
                    }
                    n -> {
                        storeBreak.text = getString(R.string.store_diff_break)
                        storeBreak.visibility = View.VISIBLE

                        var strBreak = ""
                        when(y) {
                            bTime.mon_buse -> {
                                strBreak += "/MO ${bTime.mon_starttm}~${bTime.mon_endtm}"
                            }
                            bTime.tue_buse -> {
                                strBreak += "/TU ${bTime.tue_starttm}~${bTime.tue_endtm}"
                            }
                            bTime.wed_buse -> {
                                strBreak += "/WE ${bTime.wed_starttm}~${bTime.wed_endtm}"
                            }
                            bTime.thu_buse -> {
                                strBreak += "/TH ${bTime.thu_starttm}~${bTime.thu_endtm}"
                            }
                            bTime.fri_buse -> {
                                strBreak += "/FR ${bTime.fri_starttm}~${bTime.fri_endtm}"
                            }
                            bTime.sat_buse -> {
                                strBreak += "/SA ${bTime.sat_starttm}~${bTime.sat_endtm}"
                            }
                            bTime.sun_buse -> {
                                strBreak += "/SU ${bTime.sun_starttm}~${bTime.sun_endtm}"
                            }
                        }
                        breakTime.text = strBreak.drop(1)
                        breakTime.visibility = View.VISIBLE
                    }
                    else -> { // D
                        storeBreak.visibility = View.GONE
                        breakTime.visibility = View.GONE
                    }
                }
            }
            when(store.hbuse) {
                y -> {
                    sameOff.visibility = View.VISIBLE
                    var strOff = ""
                    when(y) {
                        store.mon_buse -> strOff += "/MO"
                        store.tue_buse -> strOff += "/TU"
                        store.wed_buse -> strOff += "/WE"
                        store.thu_buse -> strOff += "/TH"
                        store.fri_buse -> strOff += "/FR"
                        store.sat_buse -> strOff += "/SA"
                        store.sun_buse -> strOff += "/SU"
                    }
                    off.text = strOff.drop(1)
                    off.visibility = View.VISIBLE
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
                    hol += if(it.month == "") { " Every Month ${it.day}" }else { " ${it.month}/${it.day}" }

                    strHoliday += "/$hol"
                }

                holiday.text = strHoliday.drop(1)
                holiday.visibility = View.VISIBLE
                storeHoliday.visibility = View.VISIBLE
            }else {
                holiday.visibility = View.GONE
                storeHoliday.visibility = View.GONE
            }
        }
    }

    fun save() {
        store.name = binding.etName.text.toString()
        store.subname = binding.etName2.text.toString()
        store.address = binding.etAddr.text.toString()

        if(store.name.isEmpty()) {
            Toast.makeText(mActivity, R.string.store_name_hint, Toast.LENGTH_SHORT).show()
        }
//        else if (store.address.isEmpty()) {
//            Toast.makeText(mActivity, R.string.msg_no_addr, Toast.LENGTH_SHORT).show()
//        }
        else {
            ApiClient.service.regStore(useridx, store.name, store.subname, store.address, store.long, store.lat)
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
        store.subname = binding.etName2.text.toString()
        store.address = binding.etAddr.text.toString()
        if(store.name.isEmpty()) {
            Toast.makeText(this@StoreSetActivity, R.string.store_name_hint, Toast.LENGTH_SHORT).show()
        }
//        else if (store.address.isEmpty()) {
//            Toast.makeText(this@StoreSetActivity, R.string.msg_no_addr, Toast.LENGTH_SHORT).show()
//        }
        else {
            ApiClient.service.udtStore(useridx, storeidx, store.name, store.subname, store.address, store.long, store.lat)
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