package com.wooriyo.pinmenuer.qrcode

import android.Manifest
import android.app.DownloadManager
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.MyApplication.Companion.engStoreName
import com.wooriyo.pinmenuer.MyApplication.Companion.store
import com.wooriyo.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.common.InfoDialog
import com.wooriyo.pinmenuer.common.NoticeDialog
import com.wooriyo.pinmenuer.config.AppProperties
import com.wooriyo.pinmenuer.databinding.ActivitySetQrcodeBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.QrDTO
import com.wooriyo.pinmenuer.model.QrListDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.qrcode.adapter.QrAdapter
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetQrcodeActivity : BaseActivity(), DialogInterface.OnDismissListener {
    lateinit var binding: ActivitySetQrcodeBinding

    private val permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val qrList = ArrayList<QrDTO>()
    val qrAdapter = QrAdapter(qrList)

    var qrCnt = 0
    var storeName = ""

    var bisBus = false  // 비즈니스 요금제 사용 여부
    var bisAll = false
    var bisCnt = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bisBus = store.paytype == 2

        if(!bisBus) {
            store.qrbuse = "N"
            setAllPostPay("N")
        }

        bisAll = store.qrbuse == "Y"
        binding.postPayAll.isChecked = bisAll

        qrAdapter.setOnPostPayClickListener(object: ItemClickListener {
            override fun onQrClick(position: Int, status: Boolean) {
                if(status) bisCnt++ else bisCnt--

                Log.d(TAG, "checked Toggle Cnt > $bisCnt")

                if(bisCnt == qrList.size) {
                    binding.postPayAll.isChecked = true
                    setAllPostPay("Y")
                }else {
                    if(binding.postPayAll.isChecked) {
                        binding.postPayAll.isChecked = false
                    }
                    val buse = if(status) "Y" else "N"
                    setPostPay(qrList[position].idx, buse, position)
                }
            }
        })

        binding.rvQr.run {
            layoutManager = GridLayoutManager(context, 5)
            adapter = qrAdapter
        }

        binding.run {
            back.setOnClickListener { finish() }
            saveName.setOnClickListener { udtStoreName() }
            downAll.setOnClickListener {
                if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    checkPermissions()
                }else {
                    downloadAll()
                }
            }
            postPayAll.setOnClickListener {
                if(bisBus) {
                    it as CheckBox
                    val buse = if(it.isChecked) "Y" else "N"
                    setAllCheck(buse)
                    setAllPostPay(buse)
                }else {
                    it as CheckBox
                    it.isChecked = false
                    InfoDialog(mActivity, "", getString(R.string.dialog_no_business)).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getQrList()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        getQrList()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(grantResults.isEmpty()) return

        if(requestCode == AppProperties.REQUEST_STORAGE) {downloadAll()}
    }

    // 이미지 접근 권한 확인
    private fun checkPermissions() {
        val deniedPms = ArrayList<String>()

        for (pms in permission) {
//            when {
//                ActivityCompat.checkSelfPermission(mActivity, pms) != PackageManager.PERMISSION_GRANTED -> deniedPms.add(pms)
//
//                ActivityCompat.shouldShowRequestPermissionRationale(mActivity, pms) -> {
//                    AlertDialog.Builder(mActivity)
//                        .setTitle(R.string.pms_storage_title)
//                        .setMessage(R.string.pms_storage_content)
//                        .setPositiveButton(R.string.confirm) { dialog, _ ->
//                            dialog.dismiss()
//                            getStoragePms()
//                        }
//                        .setNegativeButton(R.string.cancel) {dialog, _ -> dialog.dismiss()}
//                        .show()
//                    return
//                }
//            }

            if(ActivityCompat.checkSelfPermission(mActivity, pms) != PackageManager.PERMISSION_GRANTED) {
                deniedPms.add(pms)
            }
        }

        if(deniedPms.isNotEmpty()) {
            getStoragePms()
        }else {
            downloadAll()
        }
    }

    //권한 받아오기
    fun getStoragePms() {
        ActivityCompat.requestPermissions(mActivity, permission, AppProperties.REQUEST_STORAGE)
    }

    private fun setAllCheck(buse: String) {
        bisCnt = if(buse == "Y") qrList.size else 0

        for(i in 0 until qrList.size) {
            if(qrList[i].qrbuse != buse) {
                qrList[i].qrbuse = buse
                qrAdapter.notifyItemChanged(i)
            }
        }
    }

    fun downloadAll() {
        val manager = mActivity.getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        qrList.forEachIndexed { i, it ->
            val uri = Uri.parse(it.filePath.trim())
            var fileName = "${AppHelper.intToString(i+1)}_${it.tableNo}.png"
            if(engStoreName.isNotEmpty()) {
                fileName = "${engStoreName}_" + fileName
            }

            if(qrCnt < i+1) {
                return
            }

            val request = DownloadManager.Request(uri)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) //진행 중, 완료 모두 노티 보여줌
            request.setTitle("핀메뉴 관리")
            request.setDescription("QR코드 다운로드 중") // [다운로드 중 표시되는 내용]
            request.setNotificationVisibility(1) // [앱 상단에 다운로드 상태 표시]
            request.setTitle(fileName) // [다운로드 제목 표시]
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName) // [다운로드 폴더 지정]

            val downloadID = manager.enqueue(request) // [다운로드 수행 및 결과 값 지정]

            val intentFilter = IntentFilter()
            intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
//            registerReceiver(DownloadReceiver(mActivity), intentFilter)
        }
    }

    fun getQrList() {
        ApiClient.imgService.getQrList(useridx, storeidx).enqueue(object : Callback<QrListDTO> {
            override fun onResponse(call: Call<QrListDTO>, response: Response<QrListDTO>) {
                Log.d(TAG, "QR 리스트 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when (result.status) {
                    1 -> {
                        qrList.clear()
                        qrList.addAll(result.qrList)

                        qrCnt = result.qrCnt
                        binding.qrCnt.text = (qrCnt - qrList.size).toString()

                        storeName = result.enname
                        engStoreName = storeName

                        if(!storeName.isNullOrEmpty()) {
                            binding.etStoreName.setText(storeName)
                        }

                        qrAdapter.setQrCount(qrCnt)
                        qrAdapter.notifyDataSetChanged()

                        bisCnt = 0
                        qrList.forEach {
                            if(it.qrbuse == "Y") bisCnt++
                        }
                    }

                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<QrListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "QR 리스트 조회 실패 >> $t")
                Log.d(TAG, "QR 리스트 조회 실패 >> ${call.request()}")
            }
        })
    }

    fun udtStoreName() {
        storeName = binding.etStoreName.text.toString()

        if(storeName.isEmpty()) {
            Toast.makeText(mActivity, R.string.store_name_hint, Toast.LENGTH_SHORT).show()
        }else {
            ApiClient.imgService.udtStoreName(useridx, storeidx, storeName).enqueue(object :
                Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "영문 매장 이름 등록 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    when (result.status) {
                        1 -> {
                            Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            engStoreName = storeName
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "영문 매장 이름 등록 실패 >> $t")
                    Log.d(TAG, "영문 매장 이름 등록 실패 >> ${call.request()}")
                }
            })
        }
    }

    fun setPostPay(qidx: Int, buse: String, position: Int) {
        ApiClient.service.setPostPay(useridx, storeidx, qidx, buse).enqueue(object :
            Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "QR 후불 결제 설정 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when (result.status) {
                    1 -> {
                        qrList[position].qrbuse = buse
                        qrAdapter.notifyItemChanged(position)
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "QR 후불 결제 설정 실패 >> $t")
                Log.d(TAG, "QR 후불 결제 설정 실패 >> ${call.request()}")
            }
        })
    }

    fun setAllPostPay(buse: String) {
        ApiClient.service.setPostPayAll(useridx, storeidx, buse).enqueue(object :
            Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "QR 후불 결제 전체 설정 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when (result.status) {
                    1 -> {
                        store.qrbuse = buse
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "QR 후불 결제 전체 설정 실패 >> $t")
                Log.d(TAG, "QR 후불 결제 전체 설정 실패 >> ${call.request()}")
            }
        })
    }
}