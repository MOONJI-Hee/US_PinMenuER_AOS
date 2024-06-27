package com.wooriyo.us.pinmenuer.common

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.wooriyo.us.pinmenuer.BaseDialogFragment
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.databinding.DialogConfirmBinding

class UpdateDialog(private val update: Int, val msg: String): BaseDialogFragment() {
    lateinit var binding: DialogConfirmBinding
    lateinit var onClickListener: OnClickListener
    val TAG = "UpdateDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DialogConfirmBinding.inflate(layoutInflater)

        if(update == 0) {   // 권장 업데이트
            binding.cancel.run {
                text = "Do it later"
                setOnClickListener {
                    onClickListener.onClick(it)
                    dismiss()
                }
            }
        }else if (update == 1) {
            binding.cancel.visibility = View.GONE
        }

        binding.content.text = msg
        binding.confirm.text = requireContext().getString(R.string.update)

        binding.confirm.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context?.packageName}")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context?.packageName}")))
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        dismiss()
    }

    fun setCancelClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
}