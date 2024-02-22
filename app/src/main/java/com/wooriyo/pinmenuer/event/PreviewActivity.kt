package com.wooriyo.pinmenuer.event

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.databinding.ActivityPreviewBinding

class PreviewActivity : BaseActivity() {

    lateinit var binding: ActivityPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imgUri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("imgUri", Uri::class.java)
            } else {
                intent.getParcelableExtra("imgUri")
            }
        val exp = intent.getStringExtra("exp")
        val link = intent.getStringExtra("link")

        Log.d(TAG, "안녕하시소 > $imgUri")

        if(imgUri == null) {
            binding.txtPopup.visibility = View.VISIBLE
            binding.txtPopupExp.text = exp
        }else {
            binding.imgPopup.visibility = View.VISIBLE
            binding.imgPopupExp.text = exp
            Glide.with(mActivity)
                .load(imgUri)
                .transform(CenterCrop(), RoundedCorners((3* MyApplication.density).toInt()))
                .into(binding.img)
        }

        binding.back.setOnClickListener { finish() }
        binding.confirm.setOnClickListener { finish() }
    }
}