package com.wooriyo.pinmenuer.qrcode.dialog

import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Context.DOWNLOAD_SERVICE
import android.content.DialogInterface
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
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

    val TAG = "QrDetailDialog"

    var strSeq = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogQrcodeDetailBinding.inflate(layoutInflater)

        strSeq = if(seq == 0) "예약" else AppHelper.intToString(seq)
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
        }

        binding.run {
            if(seq == 0) {
                etTableNo.setText(R.string.reservation)
                etTableNo.isEnabled = false

                download.text = getString(R.string.qr_down_reserv)
                copyLink.visibility = View.VISIBLE

                clUdt.visibility = View.INVISIBLE
                confirm.visibility = View.VISIBLE

                tvPostPay.visibility = View.GONE
                postPay.visibility = View.INVISIBLE

                tvPgStatus.visibility = View.VISIBLE
                pgStatus.visibility = View.VISIBLE
                pgStatus.text =
                    if(MyApplication.store.mid.isNullOrEmpty() || MyApplication.store.mid_key.isNullOrEmpty()) getString(R.string.qr_reserv_pg_unable) else getString(R.string.able)

                binding.qrInfoArea.layoutResource = R.layout.qr_info_reserv
            }else {
                binding.postPay.isChecked = qrCode?.qrbuse == "Y"
                binding.qrInfoArea.layoutResource = R.layout.qr_info
            }

            qrInfoArea.inflate()

            close.setOnClickListener { dismiss() }
            confirm.setOnClickListener { dismiss() }
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
            copyLink.setOnClickListener {
                val clipboardManager = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(ClipData.newPlainText("url", qrCode?.url))

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                    Toast.makeText(context, R.string.msg_copy, Toast.LENGTH_SHORT).show()
            }
            postPay.setOnClickListener {
                it as CheckBox
                if(MyApplication.store.paytype == 2) {
                    qrCode?.qrbuse = if(it.isChecked) "Y" else "N"
                }else {
                    it.isChecked = false
                    InfoDialog(requireContext(), "", requireContext().getString(R.string.dialog_no_business)).show()
                }
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (context is DialogInterface.OnDismissListener) {
            (context as DialogInterface.OnDismissListener).onDismiss(dialog)
        }
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
                        qrCode = QrDTO(result.qidx, MyApplication.storeidx, seq, 1, result.filePath, "", "", getToday(), "N", "N")

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
        ApiClient.imgService.udtQr(MyApplication.useridx, MyApplication.storeidx, qidx, tableNo, qrCode!!.qrbuse)
            .enqueue(object : Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "Qr 수정 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when (result.status) {
                    1 -> {
                        Toast.makeText(context, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
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