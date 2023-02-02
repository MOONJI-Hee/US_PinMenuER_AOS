package com.wooriyo.pinmenuer.store

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityStoreSetTimeBinding
import com.wooriyo.pinmenuer.model.BrkTimeDTO
import com.wooriyo.pinmenuer.model.HolidayDTO
import com.wooriyo.pinmenuer.model.OpenTimeDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.store.dialog.HolidayDialog
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Response

class StoreSetTimeActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding : ActivityStoreSetTimeBinding

    var useridx = 11
    var storeidx = 2
    val openTimeDTO = OpenTimeDTO()

    val gson = Gson()
    val TAG = "StoreSetTimeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreSetTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.addHoliday.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.back -> finish()
            binding.save -> save()
            binding.addHoliday -> HolidayDialog(this@StoreSetTimeActivity, 1, useridx, storeidx).show()
        }
    }

    private fun save () {
        binding.run {
            openTimeDTO.let {
                if(toggleOpenSame.isChecked) {
                    it.buse = "Y"
                }else {
                    it.buse = "N"
                }


                when {
                    toggleOpenSame.isChecked -> it.buse = "Y"
                    toggleOpenDiff.isChecked -> it.buse = "N"
                }
            }
        }

        val opentest = OpenTimeDTO (
            "Y", "9:00", "18:00",
            "N", "", "",
            "N", "", "",
            "N", "", "",
            "N", "", "",
            "N", "", "",
            "N", "", "",
            "N", "", ""
        )
        val brktest = BrkTimeDTO (
            "Y", "15:00", "17:00",
            "N", "", "",
            "N", "", "",
            "N", "", "",
            "N", "", "",
            "N", "", "",
            "N", "", "",
            "N", "", ""
        )

        val holidayDTO = HolidayDTO (
            "Y",
            "N",
            "N",
            "N",
            "N",
            "N",
            "Y",
            "Y",
        )

        val jsonw = gson.toJson(opentest)
        val jsonb = gson.toJson(brktest)
        val jsonh = gson.toJson(holidayDTO)

        Log.d(TAG, "영업시간 json >> $jsonw")
        Log.d(TAG, "브레이크타임 json >> $jsonb")
        Log.d(TAG, "휴일 json >> $jsonh")

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
                                    finish()
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
}