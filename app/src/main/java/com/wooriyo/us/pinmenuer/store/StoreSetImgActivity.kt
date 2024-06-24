package com.wooriyo.us.pinmenuer.store

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wooriyo.us.pinmenuer.BaseActivity
import com.wooriyo.us.pinmenuer.MyApplication.Companion.density
import com.wooriyo.us.pinmenuer.MyApplication.Companion.store
import com.wooriyo.us.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.us.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.us.pinmenuer.config.AppProperties.Companion.REQUEST_STORAGE
import com.wooriyo.pinmenuer.databinding.ActivityStoreSetImgBinding
import com.wooriyo.us.pinmenuer.model.ResultDTO
import com.wooriyo.us.pinmenuer.util.ApiClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.*

class StoreSetImgActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding : ActivityStoreSetImgBinding

    var imgUri: Uri ?= null
    var file: File? = null
    var delImg = 0

    //registerForActivityResult
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
        binding = ActivityStoreSetImgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(store.img.isNotEmpty()) {
            setImage(store.img.toUri())
        }
        if(!store.content.isNullOrEmpty()) {
            binding.etStoreExp.setText(store.content)
        }

        binding.back.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.regImg.setOnClickListener(this)
        binding.delImg.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.back -> finish()
            binding.save -> save()
            binding.regImg -> checkPms()
            binding.delImg -> delImage()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_STORAGE) {getImage()}
    }

    private fun save() {
        var body: MultipartBody.Part? = null

        if(imgUri != null) {
            var path = ""
            var name = ""

            contentResolver.query(imgUri!!, null, null, null, null)?.use { cursor ->
                // Cache column indices.
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    path = cursor.getString(dataColumn)
                    name = cursor.getString(nameColumn)
                    Log.d(TAG, "name >>>>> $name")
                    Log.d(TAG, "path >>>>> $path")
                }
            }

            val file = File(path)
            body = MultipartBody.Part.createFormData("img", file.name, RequestBody.create(MediaType.parse("image/*"), file))

            Log.d(TAG, "이미지 Uri >> $imgUri")
            Log.d(TAG, "이미지 절대경로 >> $path")
            Log.d(TAG, "이미지 File >> $file")
            Log.d(TAG, "이미지 body >> $body")
        }
        val exp = binding.etStoreExp.text.toString()
        val expBody = RequestBody.create(MediaType.parse("text/plain"), exp)

        ApiClient.service.udtStoreImg(useridx, storeidx, body, delImg, expBody)
            .enqueue(object: retrofit2.Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "매장 대표 사진 등록 url : $response")
                    if(response.isSuccessful) {
                        val resultDTO = response.body() ?: return
                        when(resultDTO.status) {
                            1 -> {
                                Toast.makeText(this@StoreSetImgActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                                setResult(RESULT_OK)
                            }
                            else -> Toast.makeText(this@StoreSetImgActivity, resultDTO.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(this@StoreSetImgActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "매장 대표 사진 등록 실패 > $t")
                }
            })
    }

    // 외부저장소 권한 확인
    fun checkPms() {
        when {
            ContextCompat.checkSelfPermission(this@StoreSetImgActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> getImage()
            ActivityCompat.shouldShowRequestPermissionRationale(this@StoreSetImgActivity, Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                AlertDialog.Builder(this@StoreSetImgActivity)
                    .setTitle(R.string.pms_storage_title)
                    .setMessage(R.string.pms_storage_content)
                    .setPositiveButton(R.string.confirm) { dialog, _ ->
                        dialog.dismiss()
                        getPms()
                    }
                    .setNegativeButton(R.string.cancel) {dialog, _ -> dialog.dismiss()}
                    .show()
            }
            else -> getPms()
        }
    }

    // 외부저장소 권한 받아오기
    fun getPms() {
        ActivityCompat.requestPermissions(this@StoreSetImgActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_STORAGE)
    }

    fun getImage() {
        if(ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(mActivity)) {
            pickImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }else {
            pickImg_lgc.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        }
    }

    private fun setImage(imgUri: Uri) {
        binding.img.visibility = View.VISIBLE
        binding.delImg.visibility = View.VISIBLE
        Glide.with(this@StoreSetImgActivity)
            .load(imgUri)
            .transform(CenterCrop(), RoundedCorners(6 * density.toInt()))
            .into(binding.img)
        binding.imgDefault.visibility = View.INVISIBLE
        delImg = 0
    }

    fun delImage() {
        binding.img.visibility = View.INVISIBLE
        binding.delImg.visibility = View.INVISIBLE
        binding.imgDefault.visibility = View.VISIBLE
        imgUri = null
        delImg = 1
    }

    // 내부저장소에 카피해서 절대경로 불러오기
    fun getCopyPath(uri: Uri): String? {
        // 파일 경로를 만듬
        val filePath: String = (this@StoreSetImgActivity.applicationInfo.dataDir + File.separator
                + System.currentTimeMillis())
        val file = File(filePath)
        try {
            // 매개변수로 받은 uri 를 통해  이미지에 필요한 데이터를 불러 들인다.
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            // 이미지 데이터를 다시 내보내면서 file 객체에  만들었던 경로를 이용한다.
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()
        } catch (ignore: IOException) {
            return null
        }
        return file.absolutePath
    }
}