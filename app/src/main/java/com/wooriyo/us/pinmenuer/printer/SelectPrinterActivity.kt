package com.wooriyo.us.pinmenuer.printer

import android.os.Bundle
import android.view.View
import com.wooriyo.us.pinmenuer.BaseActivity
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.databinding.ActivityPrinterModelListBinding


class SelectPrinterActivity : BaseActivity() {
    lateinit var binding: ActivityPrinterModelListBinding

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
            printerInfo.visibility = View.GONE
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