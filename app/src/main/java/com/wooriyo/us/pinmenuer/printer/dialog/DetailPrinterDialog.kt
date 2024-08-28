package com.wooriyo.us.pinmenuer.printer.dialog

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.us.pinmenuer.BaseDialog
import com.wooriyo.us.pinmenuer.MyApplication
import com.wooriyo.us.pinmenuer.MyApplication.Companion.androidId
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.databinding.DialogPtrinterBinding
import com.wooriyo.us.pinmenuer.model.ResultDTO
import com.wooriyo.us.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailPrinterDialog(context: Context, val printer: BluetoothDevice, val img: Int, val model: String): BaseDialog(context) {
    lateinit var binding: DialogPtrinterBinding
    val TAG = "DetailPrinterDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DialogPtrinterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivPrinter.setImageResource(img)
        binding.model.text = model
        binding.etNick.setText(printer.alias)

        binding.close.setOnClickListener { dismiss() }
        binding.delete.setOnClickListener { delete() }
        binding.save.setOnClickListener { save() }
    }

    fun save() {
        val nick = binding.etNick.text.toString()
        ApiClient.service.setPrintNick(MyApplication.useridx, MyApplication.storeidx, androidId, nick, 2)
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "프린터 별명 설정 URL >> $response")
                    if (!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status) {
                        1 -> {
                            Toast.makeText(context, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                        else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "프린터 별명 설정 오류 >> $t")
                    Log.d(TAG, "프린터 별명 설정 오류 >> ${call.request()}")
                }
            })
    }

    fun delete() {
        // 삭제 전 연결 해제
//        if (MyApplication.bluetoothPort.isConnected) MyApplication.bluetoothPort.disconnect()

        // TODO 연결 해제 > 페어링 해제 > remote list에서 제거 > finish()
    }
}