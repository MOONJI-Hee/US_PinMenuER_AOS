package com.wooriyo.pinmenuer.store

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityStoreSetImgBinding
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.model.StoreDTO
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.*

class StoreSetImgActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding : ActivityStoreSetImgBinding
    val TAG = "StoreSetImgActivity"
    val REQUEST_R_STORAGE = 1

    var imgUri: Uri ?= null

    var useridx = 0
    var storeidx = 0
    lateinit var store: StoreDTO

    //registerForActivityResult
    val chooseImg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//        imgUri = it

        if(it.resultCode == Activity.RESULT_OK) {
            imgUri = it.data?.data
        }
        setImage()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreSetImgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        useridx = MyApplication.pref.getUserIdx()

        store = intent.getSerializableExtra("store") as StoreDTO
        storeidx = store.idx

        if(store.img.isNotEmpty()) {
            imgUri = store.img.toUri()
            setImage()
        }

        binding.back.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.regImg.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.back -> finish()
            binding.save -> save()
            binding.regImg -> checkPms()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_R_STORAGE) {getImage()}
    }

    private fun save() {
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

//            path = getCopyPath(imgUri!!)!!

            val file = File(path)
            val body = MultipartBody.Part.createFormData("img", file.name, RequestBody.create(MediaType.parse("image/*"), file))

            Log.d(TAG, "이미지 Uri >> $imgUri")
            Log.d(TAG, "이미지 절대경로 >> $path")
            Log.d(TAG, "이미지 File >> $file")
            Log.d(TAG, "이미지 body >> $body")

            ApiClient.service.udtStoreImg(useridx, storeidx, body)
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
        } else {
            Toast.makeText(this@StoreSetImgActivity, R.string.msg_no_img, Toast.LENGTH_SHORT).show()
        }
    }

    // 외부저장소 권한 확인
    fun checkPms() {
//        if(MyApplication.osver >= Build.VERSION_CODES.Q) {
//
//        }else {
//
//        }
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
        ActivityCompat.requestPermissions(this@StoreSetImgActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_R_STORAGE)
    }

    fun getImage() {
//        chooseImg.launch("image/*")
        chooseImg.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
    }

    fun setImage() {
        //        binding.img.setImageURI(it)
        binding.img.visibility = View.VISIBLE
        Glide.with(this@StoreSetImgActivity)
            .load(imgUri)
            .transform(CenterCrop(), RoundedCorners(6))
            .into(binding.img)
        binding.imgDefault.visibility = View.INVISIBLE
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