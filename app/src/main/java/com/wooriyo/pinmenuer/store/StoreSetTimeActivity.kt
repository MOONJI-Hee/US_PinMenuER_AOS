package com.wooriyo.pinmenuer.store

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityStoreSetTimeBinding
import com.wooriyo.pinmenuer.listener.DialogListener
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.*
import com.wooriyo.pinmenuer.store.adpter.SpcHolidayAdapter
import com.wooriyo.pinmenuer.store.dialog.HolidayDialog
import com.wooriyo.pinmenuer.store.dialog.RegTimeDialog
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import org.json.JSONArray
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Response

class StoreSetTimeActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding : ActivityStoreSetTimeBinding

    lateinit var store : StoreDTO
    var openTime = OpenTimeDTO()
    var breakTime = BrkTimeDTO()
    var holiday = HolidayDTO()

    var spcHoliday = ArrayList<SpcHolidayDTO>()
    val spcHolidayAdapter = SpcHolidayAdapter(spcHoliday)

    val gson = Gson()
    val TAG = "StoreSetTimeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreSetTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        spcHolidayAdapter.setOnItemClick(object : ItemClickListener {
            override fun onItemClick(position: Int) {
                spcHolidayDialog(2, spcHoliday[position])
            }
        })

        binding.rvSpecial.apply {
            layoutManager = LinearLayoutManager(this@StoreSetTimeActivity, LinearLayoutManager.VERTICAL, false)
            adapter = spcHolidayAdapter
        }

        useridx = MyApplication.pref.getUserIdx()

        store = intent.getSerializableExtra("store") as StoreDTO
        storeidx = store.idx

        if(store.opentime != null) {
            openTime = store.opentime!!
        }
        if(store.breaktime != null) {
            breakTime = store.breaktime!!
        }
        if(store.spcHoliday != null) {
            spcHoliday.addAll(store.spcHoliday!!)
        }

        // 클릭 이벤트
        binding.run {
            llOpenSame.setOnClickListener{timeDialog(open, close)}
            llOpenMon.setOnClickListener{timeDialog(openMon, closeMon)}
            llOpenTue.setOnClickListener{timeDialog(openThu, closeThu)}
            llOpenWed.setOnClickListener{timeDialog(openWed, closeWed)}
            llOpenThu.setOnClickListener{timeDialog(openThu, closeThu)}
            llOpenFri.setOnClickListener{timeDialog(openFri, closeFri)}
            llOpenSat.setOnClickListener{timeDialog(openSat, closeSat)}
            llOpenSun.setOnClickListener{timeDialog(openSun, closeSun)}
            llBrk.setOnClickListener{timeDialog(brkStart, brkEnd)}
            llBrkMon.setOnClickListener{timeDialog(brkStartMon, brkEndMon)}
            llBrkTue.setOnClickListener{timeDialog(brkStartTue, brkEndTue)}
            llBrkWed.setOnClickListener{timeDialog(brkStartWed, brkEndWed)}
            llBrkThu.setOnClickListener{timeDialog(brkStartThu, brkEndThu)}
            llBrkFri.setOnClickListener{timeDialog(brkStartFri, brkEndFri)}
            llBrkSat.setOnClickListener{timeDialog(brkStartSat, brkEndSat)}
            llBrkSun.setOnClickListener{timeDialog(brkStartSun, brkEndSun)}

            toggleOpenSame.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    toggleOpenDiff.isChecked = false
                    disOpenSame.visibility = View.GONE
                    disOpenDiff.visibility = View.VISIBLE
                }
            }
            toggleOpenDiff.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    toggleOpenSame.isChecked = false
                    disOpenSame.visibility = View.VISIBLE
                    disOpenDiff.visibility = View.GONE
                }
            }
            toggleBrkSame.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    toggleBrkDiff.isChecked = false
                    disBrkSame.visibility = View.GONE
                    disBrkDiff.visibility = View.VISIBLE
                }
            }
            toggleBrkDiff.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    toggleBrkSame.isChecked = false
                    disBrkSame.visibility = View.VISIBLE
                    disBrkDiff.visibility = View.GONE
                }
            }
            toggleHolidaySame.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked)
                    disHolidaySame.visibility = View.GONE
                else
                    disHolidaySame.visibility = View.VISIBLE
            }
        }

        binding.back.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.addHoliday.setOnClickListener(this)

        setView()
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.back -> finish()
            binding.save -> save()
            binding.addHoliday -> spcHolidayDialog(1, null)
        }
    }

    // 기존 매장 정보 보여주기
    private fun setView() {
        spcHolidayAdapter.notifyDataSetChanged()

        binding.run {
            openTime.let {
                if(it.buse == y) toggleOpenSame.isChecked = true else toggleOpenDiff.isChecked = true
                checkOpenMon.isChecked = it.mon_buse == y
                checkOpenTue.isChecked = it.tue_buse == y
                checkOpenWed.isChecked = it.wed_buse == y
                checkOpenThu.isChecked = it.thu_buse == y
                checkOpenFri.isChecked = it.fri_buse == y
                checkOpenSat.isChecked = it.sat_buse == y
                checkOpenSun.isChecked = it.sun_buse == y

                if(it.starttm!="") open.text = it.starttm
                if(it.endtm!="") close.text = it.endtm

                if(it.mon_starttm!="") openMon.text=it.mon_starttm
                if(it.mon_endtm!="") closeMon.text = it.mon_endtm

                if(it.tue_starttm!="") openTue.text = it.tue_starttm
                if(it.tue_endtm!="") closeTue.text = it.tue_endtm

                if(it.wed_starttm!="") openWed.text = it.wed_starttm
                if(it.wed_endtm!="")closeWed.text = it.wed_endtm

                if(it.thu_starttm!="") openThu.text = it.thu_starttm
                if(it.thu_endtm!="") closeThu.text = it.thu_endtm

                if(it.fri_starttm!="") openFri.text = it.fri_starttm
                if(it.fri_endtm!="") closeFri.text = it.fri_endtm

                if(it.sat_starttm!="") openSat.text = it.sat_starttm
                if(it.sat_endtm!="") closeSat.text = it.sat_endtm

                if(it.sun_starttm!="") openSun.text = it.sun_starttm
                if(it.sun_endtm!="") closeSun.text = it.sun_endtm
            }
            breakTime.let {
                if(it.buse == y) toggleBrkSame.isChecked = true else toggleBrkDiff.isChecked = true
                checkBrkMon.isChecked = it.mon_buse == y
                checkBrkTue.isChecked = it.tue_buse == y
                checkBrkWed.isChecked = it.wed_buse == y
                checkBrkThu.isChecked = it.thu_buse == y
                checkBrkFri.isChecked = it.fri_buse == y
                checkBrkSat.isChecked = it.sat_buse == y
                checkBrkSun.isChecked = it.sun_buse == y

                if(it.starttm!="") brkStart.text = it.starttm
                if(it.endtm!="") brkEnd.text = it.endtm

                if(it.mon_starttm!="") brkStartMon.text = it.mon_starttm
                if(it.mon_endtm!="") brkEndMon.text = it.mon_endtm

                if(it.tue_starttm!="") brkStartTue.text = it.tue_starttm
                if(it.tue_endtm!="") brkEndTue.text = it.tue_endtm

                if(it.wed_starttm!="") brkStartWed.text = it.wed_starttm
                if(it.wed_endtm!="") brkEndWed.text = it.wed_endtm

                if(it.thu_starttm!="") brkStartThu.text = it.thu_starttm
                if(it.thu_endtm!="") brkEndThu.text = it.thu_endtm

                if(it.fri_starttm!="") brkStartFri.text = it.fri_starttm
                if(it.fri_endtm!="") brkEndFri.text = it.fri_endtm

                if(it.sat_starttm!="") brkStartSat.text = it.sat_starttm
                if(it.sat_endtm!="") brkEndSat.text = it.sat_endtm

                if(it.sun_starttm!="") brkStartSun.text = it.sun_starttm
                if(it.sun_endtm!="") brkEndSun.text = it.sun_endtm
            }
            toggleHolidaySame.isChecked = store.hbuse == y
            holidayMon.isChecked = store.mon_buse == y
            holidayTue.isChecked = store.tue_buse == y
            holidayWed.isChecked = store.wed_buse == y
            holidayThu.isChecked = store.thu_buse == y
            holidayFri.isChecked = store.fri_buse == y
            holidaySat.isChecked = store.sat_buse == y
            holidaySun.isChecked = store.sun_buse == y
        }
    }

    private fun save () {   // 저장
        binding.run {
            openTime.let {
                when {
                    toggleOpenSame.isChecked -> it.buse = y
                    toggleOpenDiff.isChecked -> it.buse = n
                }
                if(checkOpenMon.isChecked) it.mon_buse = y else it.mon_buse = n
                if(checkOpenTue.isChecked) it.tue_buse = y else it.tue_buse = n
                if(checkOpenWed.isChecked) it.wed_buse = y else it.wed_buse = n
                if(checkOpenThu.isChecked) it.thu_buse = y else it.thu_buse = n
                if(checkOpenFri.isChecked) it.fri_buse = y else it.fri_buse = n
                if(checkOpenSat.isChecked) it.sat_buse = y else it.sat_buse = n
                if(checkOpenSun.isChecked) it.sun_buse = y else it.sun_buse = n

                it.starttm = open.text.toString()
                it.endtm = close.text.toString()

                it.mon_starttm = openMon.text.toString()
                it.mon_endtm = closeMon.text.toString()

                it.tue_starttm = openTue.text.toString()
                it.tue_endtm = closeTue.text.toString()

                it.wed_starttm = openWed.text.toString()
                it.wed_endtm = closeWed.text.toString()

                it.thu_starttm = openThu.text.toString()
                it.thu_endtm = closeThu.text.toString()

                it.fri_starttm = openFri.text.toString()
                it.fri_endtm = closeFri.text.toString()

                it.sat_starttm = openSat.text.toString()
                it.sat_endtm = closeSat.text.toString()

                it.sun_starttm = openSun.text.toString()
                it.sun_endtm = closeSun.text.toString()
            }
            breakTime.let {
                when{
                    toggleBrkSame.isChecked -> it.buse = y
                    toggleBrkDiff.isChecked -> it.buse = n
                }
            if(checkBrkMon.isChecked) it.mon_buse = y else it.mon_buse = n
            if(checkBrkTue.isChecked) it.tue_buse = y else it.tue_buse = n
            if(checkBrkWed.isChecked) it.wed_buse = y else it.wed_buse = n
            if(checkBrkThu.isChecked) it.thu_buse = y else it.thu_buse = n
            if(checkBrkFri.isChecked) it.fri_buse = y else it.fri_buse = n
            if(checkBrkSat.isChecked) it.sat_buse = y else it.sat_buse = n
            if(checkBrkSun.isChecked) it.sun_buse = y else it.sun_buse = n

            it.starttm = brkStart.text.toString()
            it.endtm = brkEnd.text.toString()

            it.mon_starttm = brkStartMon.text.toString()
            it.mon_endtm = brkEndMon.text.toString()

            it.tue_starttm = brkStartTue.text.toString()
            it.tue_endtm = brkEndTue.text.toString()

            it.wed_starttm = brkStartWed.text.toString()
            it.wed_endtm = brkEndWed.text.toString()

            it.thu_starttm = brkStartThu.text.toString()
            it.thu_endtm = brkEndThu.text.toString()

            it.fri_starttm = brkStartFri.text.toString()
            it.fri_endtm = brkEndFri.text.toString()

            it.sat_starttm = brkStartSat.text.toString()
            it.sat_endtm = brkEndSat.text.toString()

            it.sun_starttm = brkStartSun.text.toString()
            it.sun_endtm = brkEndSun.text.toString()
            }
            holiday.let {
                if(toggleHolidaySame.isChecked) it.buse = y else it.buse = n
                if(holidayMon.isChecked) it.mon_buse = y else it.mon_buse = n
                if(holidayTue.isChecked) it.tue_buse = y else it.tue_buse = n
                if(holidayWed.isChecked) it.wed_buse = y else it.wed_buse = n
                if(holidayThu.isChecked) it.thu_buse = y else it.thu_buse = n
                if(holidayFri.isChecked) it.fri_buse = y else it.fri_buse = n
                if(holidaySat.isChecked) it.sat_buse = y else it.sat_buse = n
                if(holidaySun.isChecked) it.sun_buse = y else it.sun_buse = n
            }
        }

        val jsonw = gson.toJson(openTime)
        val jsonb = gson.toJson(breakTime)
        val jsonh = gson.toJson(holiday)

        Log.d(TAG, "영업시간 $jsonw")
        Log.d(TAG, "브레이크타임 $jsonb")
        Log.d(TAG, "휴일 $jsonh")

        ApiClient.service.udtStoreTime(useridx, storeidx, jsonw, jsonb, jsonh)
            .enqueue(object : retrofit2.Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "매장 영업시간 저장 url : $response")
                    if(response.isSuccessful) {
                        val resultDTO = response.body()
                        if(resultDTO != null) {
                            when(resultDTO.status) {
                                1 -> {
                                    Toast.makeText(this@StoreSetTimeActivity, resultDTO.msg, Toast.LENGTH_SHORT).show()
                                    setResult(RESULT_OK)
                                }
                                else -> Toast.makeText(this@StoreSetTimeActivity, resultDTO.msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(this@StoreSetTimeActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "매장 영업시간 저장 실패 > $t")
                }
            })
    }

    // 시간 설정 다이얼로그
    fun timeDialog(tvStart : TextView, tvEnd: TextView) {
        val dialog = RegTimeDialog(this@StoreSetTimeActivity, tvStart.text.toString(), tvEnd.text.toString())
        dialog.setOnTimeSetListener(object: DialogListener{
            override fun onTimeSet(start: String, end: String) {
                tvStart.text = start
                tvEnd.text = end
                dialog.dismiss()
            }
        })
        dialog.show()
    }

    // 특별 휴일 다이얼로그
    fun spcHolidayDialog(type: Int, spcHolidayDTO: SpcHolidayDTO?) { // type 1: 추가 2: 수정
        val dialog = HolidayDialog(this@StoreSetTimeActivity, type, storeidx, spcHolidayDTO)
        dialog.setOnHolidaySetListener(object : DialogListener{
            override fun onHolidaySet(data: SpcHolidayDTO) {
                setResult(RESULT_OK)
            }
        })
        dialog.show()
    }
}