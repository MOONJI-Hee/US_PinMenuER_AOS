package com.wooriyo.pinmenuer.store.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogHolidayBinding
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.Encoder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HolidayDialog(context: Context, val type : Int, val useridx : Int, val idx : Int): Dialog(context), View.OnClickListener {
    lateinit var binding : DialogHolidayBinding
    val TAG = "HolidayDialog"

    var storeidx = 0
    var holidayidx = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogHolidayBinding.inflate(layoutInflater)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(binding.root)

        when(type) {
            1 -> {  // 저장
                storeidx = idx
            }
            2 -> {  // 수정
                binding.save.visibility = View.GONE
                binding.llUdt.visibility = View.VISIBLE

                holidayidx = idx
            }
        }

        binding.save.setOnClickListener(this)
        binding.delete.setOnClickListener(this)
        binding.modify.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        binding.run {
            when(p0) {
                save -> save()
                delete -> delete()
                modify -> modify()
            }
        }
    }

    private fun save() {
        val title = "지희 생일"
        val month = "10"
        val day = "22"

        Log.d(TAG, "title : $title, month : $month, day : $day")
        Log.d(TAG, "title 인코딩 했을 때 뭔데 >> ${Encoder.encodeUTF8(title)}")

        ApiClient.service.insHoliday(useridx, storeidx, title, month, day)
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "특별 휴일 등록 url : $response")
                    if(response.isSuccessful) {
                        val resultDTO = response.body()
                        if(resultDTO != null) {
                            when(resultDTO.status) {
                                1-> {
                                    Toast.makeText(context, resultDTO.msg, Toast.LENGTH_SHORT).show()
                                    dismiss()
                                }
                                else -> Toast.makeText(context, resultDTO.msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "특별 휴일 등록 실패 > $t")
                }
            })
    }

    private fun modify() {
        val title = "특별 휴일"
        val month = "00"
        val day = "15"
        ApiClient.service.udtHoliday(useridx, holidayidx, title, month, day)
            .enqueue(object : Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "특별 휴일 수정 url : $response")
                    if(response.isSuccessful) {
                        val resultDTO = response.body()
                        if(resultDTO != null) {
                            when(resultDTO.status) {
                                1-> {
                                    Toast.makeText(context, resultDTO.msg, Toast.LENGTH_SHORT).show()
                                    dismiss()
                                }
                                else -> Toast.makeText(context, resultDTO.msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "특별 휴일 수정 실패 > $t")
                }
            })
    }

    private fun delete() {
        ApiClient.service.delHoliday(useridx, holidayidx)
            .enqueue(object : Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "특별 휴일 삭제 url : $response")
                    if(response.isSuccessful) {
                        val resultDTO = response.body()
                        if(resultDTO != null) {
                            when(resultDTO.status) {
                                1-> {
                                    Toast.makeText(context, resultDTO.msg, Toast.LENGTH_SHORT).show()
                                    dismiss()
                                }
                                else -> Toast.makeText(context, resultDTO.msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "특별 휴일 삭제 실패 > $t")
                }
            })
    }
}