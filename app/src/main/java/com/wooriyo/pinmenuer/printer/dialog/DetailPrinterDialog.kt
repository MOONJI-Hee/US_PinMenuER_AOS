package com.wooriyo.pinmenuer.printer.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseDialog
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.MyApplication.Companion.androidId
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogPtrinterBinding
import com.wooriyo.pinmenuer.listener.DialogListener
import com.wooriyo.pinmenuer.model.PrintDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailPrinterDialog(context: Context, val printer: PrintDTO): BaseDialog(context) {
    lateinit var binding: DialogPtrinterBinding
//    lateinit var dialogListener: DialogListener

    val TAG = "DetailPrinterDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DialogPtrinterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var cmp = ""
        var img = 0
        when(printer.printType) {
            1 -> {
                cmp = "세우테크"
                img = R.drawable.skl_ts400b
            }
            2-> {
                cmp = "세우테크"
                img = R.drawable.skl_te202
            }
            3 -> {
                cmp = "GCUBE"
                img = R.drawable.sam4s
            }
        }

        binding.ivPrinter.setImageResource(img)

        binding.model.text = "$cmp ${printer.model}"
        if(printer.nick != "") {
            binding.etNick.setText(printer.nick)
        }

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
                            printer.nick = nick
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
        ApiClient.service.delPrint(useridx, printer.storeidx, androidId, printer.idx).enqueue(object :
            Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "프린터 삭제 URL : $response")
                if(!response.isSuccessful) return

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
                Log.d(TAG, "프린터 삭제 오류 >> $t")
                Log.d(TAG, "프린터 삭제 오류 >> ${call.request()}")
            }
        })
    }

//    fun setOnDialogListener(dialogListener: DialogListener) {
//        this.dialogListener = dialogListener
//    }
}