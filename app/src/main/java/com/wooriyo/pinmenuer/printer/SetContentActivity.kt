package com.wooriyo.pinmenuer.printer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivitySetContentBinding
import com.wooriyo.pinmenuer.model.PrintContentDTO
import com.wooriyo.pinmenuer.printer.adapter.SelCateAdapter
import com.wooriyo.pinmenuer.printer.adapter.SelectedCateAdapter
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetContentActivity : BaseActivity() {
    lateinit var binding: ActivitySetContentBinding

    val TAG = "SetContentActivity"
    val mActivity = this@SetContentActivity

    var cnt = 0

    val selectCate = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val strCate = it.data?.getStringExtra("strCate") ?: return@registerForActivityResult
            Log.d(TAG, "카테고리 설정 후 돌아옴 + strCate >>>  $strCate")
            setCategory(strCate)
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
            nick.text = getString(R.string.printer_nick_format).format(setting.nick)

            if(setting.fontSize == 1)
                rdBig.isChecked = true
            else if(setting.fontSize == 2)
                rdSmall.isChecked = true

            binding.kitchen.isChecked = setting.kitchen == "Y"
            customer.isChecked = setting.receipt == "Y"
            orderNo.isChecked = setting.ordcode == "Y"

            setCategory(setting.category?:"")
        }
    }

    fun setCategory(strCate: String) {
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

        ApiClient.service.setPrintContent(MyApplication.bidx, MyApplication.storeidx, fontSize, strKitchen, strReceipt, strOrdCode, "")
            .enqueue(object : Callback<PrintContentDTO> {
                override fun onResponse(call: Call<PrintContentDTO>, response: Response<PrintContentDTO>) {
                    Log.d(TAG, "프린터 출력 내용 설정 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status) {
                        1 -> {
                            Toast.makeText(mActivity, R.string.complete, Toast.LENGTH_SHORT).show()
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
        ApiClient.service.getPrintContentSet(MyApplication.storeidx).enqueue(object : Callback<PrintContentDTO>{
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