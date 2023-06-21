package com.wooriyo.pinmenuer.printer

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication.Companion.androidId
import com.wooriyo.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityPrinterModelListBinding
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SelectPrinterActivity : BaseActivity() {
    lateinit var binding: ActivityPrinterModelListBinding

    val TAG = "SelectPrinterActivity"
    val mActivity = this@SelectPrinterActivity

    var type = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrinterModelListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.run {
            // SupportPrinterActivity와 Layout 같이 쓰기 때문에 뷰 변경해주기
            title.text = getString(R.string.printer_title_sel_model)
            ckTs400b.visibility = View.VISIBLE
            ckTe202.visibility = View.VISIBLE
            ckSam4s.visibility = View.VISIBLE
            select.visibility = View.VISIBLE
            supportInfo.visibility = View.GONE

            ts400b.setOnClickListener {
                ckTs400b.isChecked = true
            }
            te202.setOnClickListener {
                ckTe202.isChecked = true
            }
            ckSam4s.setOnClickListener {
                ckSam4s.isChecked = true
            }

            ckTs400b.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    ckTe202.isChecked = false
                    ckSam4s.isChecked = false
                }
            }

            ckTe202.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    ckTs400b.isChecked = false
                    ckSam4s.isChecked = false
                }
            }

            ckSam4s.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    ckTs400b.isChecked = false
                    ckTe202.isChecked = false
                }
            }

            select.setOnClickListener {
                when {
                    ckTs400b.isChecked -> type = 1
                    ckTe202.isChecked -> type = 2
                    ckSam4s.isChecked -> type = 3
                }
                intent.putExtra("printType", type)
                setResult(RESULT_OK, intent)
                finish()
            }

            back.setOnClickListener { finish() }
        }
    }
}