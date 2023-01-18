package com.wooriyo.pinmenuer.store

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityStoreSetImgBinding
import com.wooriyo.pinmenuer.util.AppHelper

class StoreSetImgActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding : ActivityStoreSetImgBinding

    val REQUEST_R_STORAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreSetImgBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private fun save() {

    }

    // 외부저장소 권한 확인
    fun checkPms() {
        if(MyApplication.osver >= Build.VERSION_CODES.Q) {

        }else {

        }

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
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
    }
}