package com.wooriyo.us.pinmenuer.common

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.wooriyo.us.pinmenuer.BaseDialog
import com.wooriyo.us.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.databinding.DialogFullscreenBinding
import com.wooriyo.us.pinmenuer.model.PopupDTO
import com.wooriyo.us.pinmenuer.util.AppHelper

class FullScreenDialog(val popup: PopupDTO) : DialogFragment() {
    lateinit var binding: DialogFullscreenBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BaseDialog(requireContext())
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogFullscreenBinding.inflate(getLayoutInflater())

        Glide.with(requireContext()).load(popup.img).transform(CenterCrop()).into(binding.img)

        binding.close.setOnClickListener{ dismiss() }

        binding.notToday.setOnClickListener{
            val today = AppHelper.getToday()
            Log.d("WelcomeDialog", "오늘 날짜 >> $today")
            MyApplication.pref.setNoPopupDate(today)
            dismiss()
        }

        // 팝업 이미지 클릭 시 해당 링크로 이동
        binding.img.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(popup.link)
            startActivity(intent)
        }
        return binding.getRoot()
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window ?: return
        val params = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = params
    }
}