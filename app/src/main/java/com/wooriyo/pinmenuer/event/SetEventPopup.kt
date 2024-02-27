package com.wooriyo.pinmenuer.event

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication.Companion.density
import com.wooriyo.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.config.AppProperties
import com.wooriyo.pinmenuer.databinding.ActivitySetEventPopupBinding
import com.wooriyo.pinmenuer.event.dialog.EventConfirmDialog
import com.wooriyo.pinmenuer.model.PopupDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SetEventPopup : BaseActivity() {
    lateinit var binding: ActivitySetEventPopupBinding

    private var imgUri: Uri? = null
    var file: File? = null
    var delImg = 0

    val radius = (6*density).toInt()

    val mtpImg = MediaType.parse("image/*")
    val mtpTxt = MediaType.parse("text/plain")

    private val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    private val pickImg = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        if(it != null) {
            Log.d(TAG, "이미지 Uri >> $it")
            imgUri = it
            setImage(it)
        }
    }

    private val pickImg_lgc = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            imgUri = it.data?.data
            if(imgUri != null) {
                setImage(imgUri!!)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetEventPopupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener { finish() }
        binding.btnSetImg.setOnClickListener { checkPermissions() }

        binding.delImg.setOnClickListener {
            delImg = 1
            file = null
            imgUri = null
            binding.img.visibility = View.INVISIBLE
            binding.delImg.visibility = View.GONE
            binding.imgDefault.visibility = View.VISIBLE
        }

        binding.preview.setOnClickListener {
            val exp = binding.exp.text.toString()
            val link = binding.link.text.toString()
            val bIsUse = binding.useEvent.isChecked

            if(exp.isEmpty())
                Toast.makeText(mActivity, R.string.msg_no_event_exp, Toast.LENGTH_SHORT).show()
            else preview(exp, link)
        }
        binding.save.setOnClickListener {
            val exp = binding.exp.text.toString()
            val link = binding.link.text.toString()
            val bIsUse = binding.useEvent.isChecked

            if(bIsUse) {
                if(exp.isEmpty()) {
                    Toast.makeText(mActivity, R.string.msg_no_event_exp, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                save(exp, link, bIsUse)
            }else {
                EventConfirmDialog { save(exp, link, bIsUse) }.show(supportFragmentManager, "EventConfirmDialog")
            }
        }

        getEvent()
    }

    // 외부저장소 접근 권한 확인
    fun checkPermissions() {
        Log.d(TAG, "권한확인 시작")
        val deniedPms = ArrayList<String>()

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            for (pms in permission) {
                if(ActivityCompat.checkSelfPermission(mActivity, pms) != PackageManager.PERMISSION_GRANTED) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity, pms)) {
                        AlertDialog.Builder(mActivity)
                            .setTitle(R.string.pms_storage_title)
                            .setMessage(R.string.pms_storage_content)
                            .setPositiveButton(R.string.confirm) { dialog, _ ->
                                dialog.dismiss()
                                getStoragePms()
                            }
                            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss()}
                            .show()
                        return
                    }else {
                        deniedPms.add(pms)
                    }
                }
            }
        }

        if(deniedPms.isNotEmpty()) {
            getStoragePms()
        }else {
            getImage()
        }
    }

    // 외부저장소 권한 받아오기
    fun getStoragePms() {
        ActivityCompat.requestPermissions(mActivity, permission, AppProperties.REQUEST_STORAGE)
    }

    fun getImage() {
        if(ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(mActivity)) {
            pickImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }else {
            pickImg_lgc.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        }
    }

    fun setImage(imgUri: Uri) {
        var path = ""

        contentResolver.query(imgUri, null, null, null, null)?.use { cursor ->
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                path = cursor.getString(dataColumn)
                Log.d(TAG, "path >>>>> $path")
            }
        }

        file = File(path)
        delImg = 0

        Glide.with(mActivity)
            .load(imgUri)
            .transform(CenterCrop(), RoundedCorners(radius))
            .into(binding.img)

        binding.img.visibility= View.VISIBLE
        binding.delImg.visibility = View.VISIBLE
        binding.imgDefault.visibility = View.GONE
    }

    private fun preview(exp: String, link: String) {
        val intent = Intent(mActivity, PreviewActivity::class.java)
        intent.putExtra("exp", exp)
        intent.putExtra("link", link)
        intent.putExtra("imgUri", imgUri)
//        intent.putExtra("strUri", )
        startActivity(intent)
    }

    private fun save(exp: String, link: String, bIsUse: Boolean) {
        var media: MultipartBody.Part? = null

        if(file != null) {
            val body = RequestBody.create(mtpImg, file!!)
            media = MultipartBody.Part.createFormData("img", file!!.name, body)
        }

        val strBuse = if(bIsUse) "Y" else "N"

        val useBody = RequestBody.create(mtpTxt, strBuse)
        val expBody = RequestBody.create(mtpTxt, exp)
        val linkBody = RequestBody.create(mtpTxt, link)


        ApiClient.service.setEventPopup(useridx, storeidx, useBody, expBody, linkBody, delImg, media)
            .enqueue(object : Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "주문완료 후 팝업 설정 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status) {
                        1 -> {
                            Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "주문완료 후 팝업 설정 실패 > $t")
                    Log.d(TAG, "주문완료 후 팝업 설정 실패 > ${call.request()}")
                }
            })
    }

    private fun getEvent() {
        ApiClient.service.getEventPopup(useridx, storeidx)
            .enqueue(object : Callback<PopupDTO> {
                override fun onResponse(call: Call<PopupDTO>, response: Response<PopupDTO>) {
                    Log.d(TAG, "주문완료 후 팝업 정보 조회 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return

                    when(result.status) {
                        1 -> {
                            binding.useEvent.isChecked = result.buse == "Y"

                            if(!(result.img.isNullOrEmpty() || (result.img?:"").contains("noimage.png"))) {
                                imgUri = (result.img)?.toUri()
                                Glide.with(mActivity)
                                    .load(imgUri)
                                    .transform(CenterCrop(), RoundedCorners(radius))
                                    .into(binding.img)
                                binding.img.visibility = View.VISIBLE
                                binding.delImg.visibility = View.VISIBLE
                                binding.imgDefault.visibility = View.GONE
                            }

                            binding.exp.setText(result.content)
                            binding.link.setText(result.link)
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PopupDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "주문완료 후 팝업 정보 조회 실페 > $t")
                    Log.d(TAG, "주문완료 후 팝업 정보 조회 실패 > ${call.request()}")
                }
            })
    }
}