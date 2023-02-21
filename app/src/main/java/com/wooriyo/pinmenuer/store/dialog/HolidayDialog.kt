package com.wooriyo.pinmenuer.store.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogHolidayBinding
import com.wooriyo.pinmenuer.listener.DialogListener
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.model.SpcHolidayDTO
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import com.wooriyo.pinmenuer.util.Encoder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HolidayDialog(context: Context, val type : Int, val useridx : Int, val storeidx : Int, val spcHoliday : SpcHolidayDTO?): Dialog(context), View.OnClickListener {
    lateinit var binding : DialogHolidayBinding
    lateinit var dialogListener: DialogListener
    val TAG = "HolidayDialog"

    var holidayidx = 0
    var title = ""
    var month = ""
    var day = ""

    fun setOnHolidaySetListener (dialogListener: DialogListener) {
        this.dialogListener = dialogListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = DialogHolidayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when(type) {
            2 -> {  // 수정
                binding.save.visibility = View.GONE
                binding.llUdt.visibility = View.VISIBLE

                if(spcHoliday != null) {
                    holidayidx = spcHoliday.idx
                    title = spcHoliday.title
                    month = spcHoliday.month
                    day = spcHoliday.day

                    if(title != "")
                        binding.etName.setText(title)
                    if(month != "")
                        binding.etMonth.setText(AppHelper.mkDouble(month))
                    if(day != "")
                        binding.etDay.setText(AppHelper.mkDouble(day))
                }
            }
        }

        binding.close.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.delete.setOnClickListener(this)
        binding.modify.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        binding.run {
            when(p0) {
                close -> dismiss()
                save -> save()
                delete -> delete()
                modify -> modify()
            }
        }
    }

    fun check() {
        title = binding.etName.text.toString()
        month = binding.etMonth.text.toString()
        day = binding.etDay.text.toString()

        if(title.isEmpty()) {
            Toast.makeText(context, R.string.msg_no_name, Toast.LENGTH_SHORT).show()
            return
        }else if(day.isEmpty()) {
            Toast.makeText(context, R.string.msg_no_date, Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun save() {
        check()
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
//                                    dialogListener.onHolidaySet()
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
        check()
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