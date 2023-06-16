package com.wooriyo.pinmenuer.printer

import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication.Companion.allCateList
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivitySelCateBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.printer.adapter.SelCateAdapter

class SelCateActivity : BaseActivity() {
    lateinit var binding: ActivitySelCateBinding

    val TAG = "SelCateActivity"
    val mActivity = this@SelCateActivity

    var cnt = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelCateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTitle()

        val cateAdapter = SelCateAdapter(allCateList)
        cateAdapter.setOnCheckClickListener(object : ItemClickListener {
            override fun onCheckClick(position: Int, v: CheckBox, isChecked: Boolean) {
                if(isChecked) {
                    if(cnt < 10) {
                        allCateList[position].checked = 1
                        cnt++
                        setTitle()
                    }else {
                        v.isChecked = false
                    }
                }else {
                    allCateList[position].checked = 0
                    if(cnt > 0) {
                        cnt--
                        setTitle()
                    }
                }
            }
        })
        binding.rvCate.layoutManager = GridLayoutManager(mActivity, 4)
        binding.rvCate.adapter = cateAdapter

        binding.back.setOnClickListener { finish() }
        binding.save.setOnClickListener { save() }
    }

    fun setTitle() {
        binding.title.text = getString(R.string.printer_title_category_set).format(cnt)
    }

    fun save() {
        var strCate = ""
        allCateList.forEach {
            if(it.checked == 1) {
                if(strCate == "")
                    strCate = it.name
                else
                    strCate += ",${it.name}"
            }
        }
        Log.d(TAG, "All Category List >> $allCateList")
        Log.d(TAG, "strCate >> $strCate")

        intent.putExtra("strCate", strCate)
        setResult(RESULT_OK, intent)
        finish()
    }


//    fun getCategory() {
//        ApiClient.service.getCateList(MyApplication.useridx, MyApplication.storeidx)
//            .enqueue(object: Callback<CateListDTO> {
//                override fun onResponse(call: Call<CateListDTO>, response: Response<CateListDTO>) {
//                    Log.d(TAG, "카테고리 조회 url : $response")
//                    if(!response.isSuccessful) {return}
//                    val result = response.body()
//                    if(result != null) {
//                        when(result.status) {
//                            1 -> {
//                                allCateList.addAll(result.cateList)
//                                cateAdapter.notifyDataSetChanged()
//                            }
//                            else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//                override fun onFailure(call: Call<CateListDTO>, t: Throwable) {
//                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
//                    Log.d(TAG, "카테고리 조회 실패 > $t")
//                    Log.d(TAG, "카테고리 조회 실패 > ${call.request()}")
//                }
//            })
//    }
}