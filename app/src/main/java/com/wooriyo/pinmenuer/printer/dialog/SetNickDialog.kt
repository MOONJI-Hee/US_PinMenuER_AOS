package com.wooriyo.pinmenuer.printer.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogSetNickBinding
import com.wooriyo.pinmenuer.listener.DialogListener
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetNickDialog(context: Context, var nick: String, val type: Int, val model: String): BaseDialog(context) {
    lateinit var binding: DialogSetNickBinding
    lateinit var dialogListener: DialogListener

    val TAG = "SetNickDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogSetNickBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(nick != "")
            binding.etNick.setText(nick)

        binding.model.text = model

        binding.close.setOnClickListener { dismiss() }
        binding.save.setOnClickListener { save() }
    }

    fun save() {
        nick = binding.etNick.text.toString()
        ApiClient.service.setPrintNick(MyApplication.storeidx, nick, type)
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "별명 설정 URL >> $response")
                    if (!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status) {
                        1 -> {
                            dialogListener.onNickSet(nick)
                            dismiss()
                        }
                        else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "별명 설정 오류 >> $t")
                    Log.d(TAG, "별명 설정 오류 >> ${call.request()}")
                }
            })
    }

    fun setOnNickChangeListener(dialogListener: DialogListener) {
        this.dialogListener = dialogListener
    }
}