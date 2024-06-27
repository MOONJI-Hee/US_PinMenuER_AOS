package com.wooriyo.us.pinmenuer.setting

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.us.pinmenuer.BaseActivity
import com.wooriyo.us.pinmenuer.MyApplication
import com.wooriyo.us.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.us.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.databinding.ActivityMenuUiBinding
import com.wooriyo.us.pinmenuer.model.ResultDTO
import com.wooriyo.us.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Response

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
                if(isChecked) {
                    check3x3.isChecked = false
                    checkList.isChecked = false
                }
            }
            check3x3.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked)  {
                    checkBasic.isChecked = false
                    checkList.isChecked = false
                }
            }
            checkList.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    checkBasic.isChecked = false
                    check3x3.isChecked = false
                }
            }

            when(MyApplication.store.viewmode) {
                "b" -> binding.checkBasic.isChecked = true
                "p" -> binding.check3x3.isChecked = true
                "l" -> binding.checkList.isChecked = true
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
            binding.checkList.isChecked -> selMode = "l"
        }

        ApiClient.service.setThema(useridx, storeidx, selColor, selMode)
            .enqueue(object: retrofit2.Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "메뉴 테마 설정 url > $response")
                    if (!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status) {
                        1 -> {
                            MyApplication.store.bgcolor = selColor
                            MyApplication.store.viewmode = selMode
                            Toast.makeText(this@MenuUiActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        }
                        else -> Toast.makeText(this@MenuUiActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(this@MenuUiActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "메뉴 테마 설정 실패 > $t")
                    Log.d(TAG, "메뉴 테마 실패 > ${call.request()}")
                }
        })

    }
}