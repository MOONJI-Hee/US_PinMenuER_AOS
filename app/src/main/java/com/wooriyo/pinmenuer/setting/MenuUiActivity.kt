package com.wooriyo.pinmenuer.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.databinding.ActivityMenuUiBinding

class MenuUiActivity : BaseActivity() {
    lateinit var binding: ActivityMenuUiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuUiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.run{
            checkBlack.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    checkSilver.isChecked = false
                    checkLight.isChecked = false
                }
            }
            checkSilver.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    checkBlack.isChecked = false
                    checkLight.isChecked = false
                }
            }
            checkLight.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    checkBlack.isChecked = false
                    checkSilver.isChecked = false
                }
            }

            when(MyApplication.store.bgcolor) {
                "d" -> checkBlack.isChecked = true
                "g" -> checkSilver.isChecked = true
                "l" -> checkLight.isChecked = true
            }

            checkBasic.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) check3x3.isChecked = false
            }
            check3x3.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) checkBasic.isChecked = false
            }

            when(MyApplication.store.viewmode) {
                "b" -> binding.checkBasic.isChecked = true
                "p" -> binding.check3x3.isChecked = true
            }

            back.setOnClickListener { finish() }
            save.setOnClickListener { save() }
        }
    }

    fun save() {
        var selColor = ""
        var selMode = ""

        when {
            binding.checkBlack.isChecked -> selColor = "d"
            binding.checkSilver.isChecked -> selColor = "g"
            binding.checkLight.isChecked -> selColor = "l"
        }

        when {
            binding.checkBasic.isChecked -> selMode = "b"
            binding.check3x3.isChecked -> selMode = "p"
        }


    }
}