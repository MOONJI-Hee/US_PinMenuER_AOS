package com.wooriyo.pinmenuer.order.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogSelectPayBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.PaySettingDTO
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectPayDialog(context: Context, val position: Int): BaseDialog(context) {
    lateinit var binding: DialogSelectPayBinding

    lateinit var qrClickListener: ItemClickListener
    lateinit var cardClickListener: ItemClickListener
    lateinit var completeClickListener: ItemClickListener

    val TAG = "SelectPayDialog"

    var qrStatus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogSelectPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPayInfo()

        binding.close.setOnClickListener { dismiss() }
        binding.btnQR.setOnClickListener {
            qrClickListener.onQrClick(position, qrStatus)
        }
        binding.btnCard.setOnClickListener {
            cardClickListener.onItemClick(position)
        }
        binding.btnComplete.setOnClickListener {
            completeClickListener.onItemClick(position)
        }
        binding.unableQR.setOnClickListener {
            dismiss()
            NoPayDialog(context, 0).show()
        }
        binding.unableCard.setOnClickListener {
            dismiss()
            NoPayDialog(context, 1).show()
        }
    }

    fun setOnQrClickListener(qrClickListener: ItemClickListener) {
        this.qrClickListener = qrClickListener
    }

    fun setOnCardClickListener(cardClickListener: ItemClickListener) {
        this.cardClickListener = cardClickListener
    }

    fun setOnCompleteClickListener(completeClickListener: ItemClickListener) {
        this.completeClickListener = completeClickListener
    }

    fun setView(settingDTO: PaySettingDTO) {
        if(settingDTO.qrbuse == "N")
            binding.unableQR.visibility = View.VISIBLE

        if(settingDTO.cardbuse == "N")
            binding.unableCard.visibility = View.VISIBLE

        qrStatus = !(settingDTO.mid.isEmpty() || settingDTO.mid_key.isEmpty())
    }

    fun getPayInfo() {
        ApiClient.service.getPayInfo(MyApplication.useridx, MyApplication.storeidx, MyApplication.androidId)
            .enqueue(object: Callback<PaySettingDTO> {
                override fun onResponse(call: Call<PaySettingDTO>, response: Response<PaySettingDTO>) {
                    Log.d(TAG, "결제 설정 조회 URL : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status) {
                        1 -> setView(result)
                        else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PaySettingDTO>, t: Throwable) {
                    Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "결제 설정 조회 오류 >> $t")
                    Log.d(TAG, "결제 설정 내용 조회 오류 >> ${call.request()}")
                }
            })
    }
}