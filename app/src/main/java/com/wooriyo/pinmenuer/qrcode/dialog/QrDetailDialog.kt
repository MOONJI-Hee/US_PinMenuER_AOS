package com.wooriyo.pinmenuer.qrcode.dialog

import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.Toast
import com.bumptech.glide.Glide
import com.wooriyo.pinmenuer.BaseDialogFragment
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.MyApplication.Companion.engStoreName
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.common.InfoDialog
import com.wooriyo.pinmenuer.databinding.DialogQrcodeDetailBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.QrDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import com.wooriyo.pinmenuer.util.AppHelper.Companion.getToday
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QrDetailDialog(val seq: Int, var qrCode: QrDTO?): BaseDialogFragment() {
    lateinit var binding: DialogQrcodeDetailBinding
    lateinit var postPayClickListener: ItemClickListener

    val TAG = "QrDetailDialog"

    var strSeq = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogQrcodeDetailBinding.inflate(layoutInflater)

        strSeq = AppHelper.intToString(seq)
        binding.tvSeq.text = strSeq

        if(qrCode == null) {
            binding.delete.isEnabled = false
            binding.save.isEnabled = false
            createQr()
        }else {
            binding.etTableNo.setText(qrCode?.tableNo)
            Glide.with(requireContext())
                .load(qrCode!!.filePath)
                .into(binding.ivQr)

            binding.postPay.isChecked = qrCode!!.qrbuse == "Y"
        }

        binding.run {
            close.setOnClickListener { dismiss() }
            postPay.setOnClickListener {
                it as CheckBox
                if(MyApplication.store.paytype == 2) {
                    postPayClickListener.onQrClick(seq-1, it.isChecked)
                }else {
                    it.isChecked = false
                    InfoDialog(requireContext(), "", requireContext().getString(R.string.dialog_no_business)).show()
                }
            }
            save.setOnClickListener {
                val tableNo = binding.etTableNo.text.toString()

                if(tableNo.isEmpty())
                    Toast.makeText(context, R.string.msg_no_table_no, Toast.LENGTH_SHORT).show()
                else {
                    if(qrCode != null) udtQr(qrCode!!.idx, tableNo)
                }
            }
            delete.setOnClickListener {
                if(qrCode != null) delQr(qrCode!!.idx)
            }
            download.setOnClickListener {
                if(qrCode == null) return@setOnClickListener

                val uri = Uri.parse(qrCode!!.filePath.trim()) // 파일 주소 : 확장자명 포함되어야함

                var fileName = "${strSeq}_${qrCode!!.tableNo}.png"
                if(engStoreName.isNotEmpty()) {
                    fileName = "${engStoreName}_" + fileName
                }

                val request = DownloadManager.Request(uri)

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) //진행 중, 완료 모두 노티 보여줌
                request.setTitle("핀메뉴 관리")
                request.setDescription("QR코드 다운로드 중") // [다운로드 중 표시되는 내용]
                request.setNotificationVisibility(1) // [앱 상단에 다운로드 상태 표시]
                request.setTitle(fileName) // [다운로드 제목 표시]
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName) // [다운로드 폴더 지정]

                val manager = requireContext().getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val downloadID = manager.enqueue(request) // [다운로드 수행 및 결과 값 지정]

                val intentFilter = IntentFilter()
                intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
//                registerReceiver(DownloadReceiver(mActivity), intentFilter)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window ?: return
        val params = window.attributes
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = params
    }

    fun setOnPostPayClickListener(postPayClickListener: ItemClickListener) {
        this.postPayClickListener = postPayClickListener
    }

    fun createQr() {
        ApiClient.imgService.createQr(MyApplication.useridx, MyApplication.storeidx, seq).enqueue(object :
            Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "Qr 생성 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when (result.status) {
                    1 -> {
                        qrCode = QrDTO(result.qidx, MyApplication.storeidx, seq, 1, result.filePath, "", getToday(), "N")

                        binding.delete.isEnabled = true
                        binding.save.isEnabled = true

                        Glide.with(requireContext())
                            .load(result.filePath)
                            .into(binding.ivQr)
                    }
                    else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Qr 생성 실패 >> $t")
                Log.d(TAG, "Qr 생성 실패 >> ${call.request()}")
            }
        })
    }

    fun udtQr(qidx: Int, tableNo: String) {
        ApiClient.imgService.udtQr(MyApplication.useridx, MyApplication.storeidx, qidx, tableNo, qrCode!!.qrbuse).enqueue(object :
            Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "Qr 수정 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when (result.status) {
                    1 -> Toast.makeText(context, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Qr 수정 실패 >> $t")
                Log.d(TAG, "Qr 수정 실패 >> ${call.request()}")
            }
        })
    }

    fun delQr(qidx: Int) {
        ApiClient.imgService.delQr(MyApplication.useridx, MyApplication.storeidx, qidx).enqueue(object :
            Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "Qr 삭제 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when (result.status) {
                    1 ->  {
                        Toast.makeText(context, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                    else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Qr 삭제 실패 >> $t")
                Log.d(TAG, "Qr 삭제 실패 >> ${call.request()}")
            }
        })
    }
}