package com.wooriyo.us.pinmenuer.printer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import com.wooriyo.us.pinmenuer.BaseActivity
import com.wooriyo.us.pinmenuer.MyApplication
import com.wooriyo.us.pinmenuer.MyApplication.Companion.androidId
import com.wooriyo.us.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivitySetContentBinding
import com.wooriyo.us.pinmenuer.model.PrintContentDTO
import com.wooriyo.us.pinmenuer.printer.adapter.SelectedCateAdapter
import com.wooriyo.us.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetContentActivity : BaseActivity() {
    lateinit var binding: ActivitySetContentBinding

    var cnt = 0
    var strCate = ""    // 주방영수증에 출력될 카테고리 리스트 (String형, 콤마로 구분)

    val selectCate = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            strCate = it.data?.getStringExtra("strCate") ?: return@registerForActivityResult
            Log.d(TAG, "카테고리 설정 후 돌아옴 + strCate >>>  $strCate")
            setCategory()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPrintSetting()

        binding.rvCate.layoutManager = GridLayoutManager(mActivity, 2)

        binding.back.setOnClickListener { finish() }
        binding.save.setOnClickListener { save() }
        binding.cateSet.setOnClickListener{
            val intent = Intent(mActivity, SelCateActivity::class.java)
            selectCate.launch(intent)
        }
    }

    fun setView(setting: PrintContentDTO) {
        binding.run {
            if(setting.admnick != null)
                nick.text = getString(R.string.printer_nick_format).format(setting.admnick)

            if(setting.fontSize == 1)
                rdBig.isChecked = true
            else if(setting.fontSize == 2)
                rdSmall.isChecked = true

            binding.kitchen.isChecked = setting.kitchen == "Y"
            customer.isChecked = setting.receipt == "Y"
            orderNo.isChecked = setting.ordcode == "Y"

            strCate = setting.category?:""
            setCategory()
        }
    }

    fun setCategory() {
        val category = strCate.split(",")
        if(category.isNotEmpty()) {
            cnt = category.size
            Log.d(TAG, "Category list >>>> $category")
            Log.d(TAG, "Category CNT >>>> $cnt")
        }else cnt = 0

        binding.cateCnt.text = getString(R.string.printer_kitchen_format).format(cnt)

        binding.rvCate.adapter = SelectedCateAdapter(category)
    }

    fun save() {
        var strKitchen = "N"
        var strReceipt = "N"
        var strOrdCode = "N"

        val fontSize = if(binding.rdBig.isChecked) 1 else 2

        if(binding.kitchen.isChecked)
            strKitchen = "Y"

        if(binding.customer.isChecked)
            strReceipt = "Y"

        if(binding.orderNo.isChecked)
            strOrdCode = "Y"

        ApiClient.service.setPrintContent(useridx,  MyApplication.storeidx, androidId, MyApplication.bidx, fontSize, strKitchen, strReceipt, strOrdCode, strCate)
            .enqueue(object : Callback<PrintContentDTO> {
                override fun onResponse(call: Call<PrintContentDTO>, response: Response<PrintContentDTO>) {
                    Log.d(TAG, "프린터 출력 내용 설정 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status) {
                        1 -> {
                            Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            setView(result)
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PrintContentDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "프린터 출력 내용 설정 오류 >> $t")
                    Log.d(TAG, "프린터 출력 내용 설정 오류 >> ${call.request()}")
                }
            })
    }

    fun getPrintSetting() {
        ApiClient.service.getPrintContentSet(useridx, MyApplication.storeidx, androidId).enqueue(object : Callback<PrintContentDTO>{
            override fun onResponse(call: Call<PrintContentDTO>, response: Response<PrintContentDTO>) {
                Log.d(TAG, "프린터 출력 내용 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> setView(result)
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PrintContentDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "프린터 출력 내용 조회 오류 >> $t")
                Log.d(TAG, "프린터 출력 내용 조회 오류 >> ${call.request()}")
            }
        })
    }
}