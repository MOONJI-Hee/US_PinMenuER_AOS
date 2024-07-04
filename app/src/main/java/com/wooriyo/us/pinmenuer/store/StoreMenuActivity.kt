package com.wooriyo.us.pinmenuer.store

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import com.wooriyo.us.pinmenuer.BaseActivity
import com.wooriyo.us.pinmenuer.MyApplication
import com.wooriyo.us.pinmenuer.MyApplication.Companion.store
import com.wooriyo.us.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.us.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.us.pinmenuer.MyApplication.Companion.allCateList
import com.wooriyo.us.pinmenuer.MyApplication.Companion.androidId
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.call.CallSetActivity
import com.wooriyo.us.pinmenuer.databinding.ActivityStoreMenuBinding
import com.wooriyo.us.pinmenuer.event.SetEventPopup
import com.wooriyo.us.pinmenuer.history.ByHistoryActivity
import com.wooriyo.us.pinmenuer.history.ByTableActivity
import com.wooriyo.us.pinmenuer.member.MemberSetActivity
import com.wooriyo.us.pinmenuer.menu.CategorySetActivity
import com.wooriyo.us.pinmenuer.menu.MenuSetActivity
import com.wooriyo.us.pinmenuer.model.CateListDTO
import com.wooriyo.us.pinmenuer.model.ResultDTO
import com.wooriyo.us.pinmenuer.pg.PgHistoryActivity
import com.wooriyo.us.pinmenuer.pg.dialog.NoPgInfoDialog
import com.wooriyo.us.pinmenuer.printer.PrinterMenuActivity
import com.wooriyo.us.pinmenuer.qrcode.SetQrcodeActivity
import com.wooriyo.us.pinmenuer.setting.MenuUiActivity
import com.wooriyo.us.pinmenuer.setting.TablePassActivity
import com.wooriyo.us.pinmenuer.tiptax.TipTaxActivity
import com.wooriyo.us.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoreMenuActivity : BaseActivity(), OnClickListener {
    lateinit var binding: ActivityStoreMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCategory()

        binding.run {
            title.text = store.name
            version.text = "Ver ${MyApplication.appver}"

            back.setOnClickListener{ leaveStore() }
            udtMbr.setOnClickListener{ startActivity(Intent(mActivity, MemberSetActivity::class.java)) }

            history.setOnClickListener{ startActivity(Intent(mActivity, ByHistoryActivity::class.java)) }
            byTable.setOnClickListener{ startActivity(Intent(mActivity, ByTableActivity::class.java)) }
            tablePass.setOnClickListener{ startActivity(Intent(mActivity, TablePassActivity::class.java)) }
            setCall.setOnClickListener { startActivity(Intent(mActivity, CallSetActivity::class.java)) }
            printer.setOnClickListener{ insPrintSetting() }
            payment.setOnClickListener{ insPaySetting() }
            pgCancel.setOnClickListener{
                if(store.pg_storenm.isEmpty() || store.pg_snum.isEmpty()) {
                    NoPgInfoDialog(mActivity).show()
                }else {
                    startActivity(Intent(mActivity, PgHistoryActivity::class.java))
                }
            }
            tiptax.setOnClickListener {
                startActivity(Intent(mActivity, TipTaxActivity::class.java))
            }
            qrcode.setOnClickListener {
                startActivity(Intent(mActivity, SetQrcodeActivity::class.java))
            }
            design.setOnClickListener{ startActivity(Intent(mActivity, MenuUiActivity::class.java)) }
            event.setOnClickListener { startActivity(Intent(mActivity, SetEventPopup::class.java)) }

            menu.setOnClickListener{
                if(allCateList.isEmpty()) {
                    val intent = Intent(mActivity, CategorySetActivity::class.java)
                    startActivity(intent)
                }else {
                    val intent = Intent(mActivity, MenuSetActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        Toast.makeText(mActivity, R.string.msg_no_pay, Toast.LENGTH_SHORT).show()
    }

    fun getCategory() {
        ApiClient.service.getCateList(useridx, storeidx)
            .enqueue(object: Callback<CateListDTO>{
                override fun onResponse(call: Call<CateListDTO>, response: Response<CateListDTO>) {
                    Log.d(TAG, "카테고리 조회 url : $response")
                    if(!response.isSuccessful) {return}
                    val result = response.body()
                    if(result != null) {
                        when(result.status) {
                            1 -> allCateList.addAll(result.cateList)
                            else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<CateListDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "카테고리 조회 실패 > $t")
                    Log.d(TAG, "카테고리 조회 실패 > ${call.request()}")
                }
            })
    }

    fun leaveStore() {
        ApiClient.service.leaveStore(useridx, storeidx, androidId)
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "매장 나가기 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    if(result.status == 1){
                        storeidx = 0
                        finish()
//                        startActivity(Intent(mActivity, StoreListActivity::class.java))
                    }else
                        Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "매장 나가기 오류 > $t")
                    Log.d(TAG, "매장 나가기 오류 > ${call.request()}")
                }
            })
    }

    fun insPrintSetting() {
        ApiClient.service.insPrintSetting(useridx, storeidx, androidId)
            .enqueue(object : Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "프린터 설정 최초 진입 시 row 추가 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return

                    if(result.status == 1){
                        MyApplication.bidx = result.bidx
                        startActivity(Intent(mActivity, PrinterMenuActivity::class.java))
                    }else
                        Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "프린터 설정 최초 진입 시 row 추가 오류 >> $t")
                    Log.d(TAG, "프린터 설정 최초 진입 시 row 추가 오류 >> ${call.request()}")
                }
            })
    }


    fun insPaySetting() {
        ApiClient.service.insPaySetting(useridx, storeidx, androidId)
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "결제 설정 최초 진입 시 row 추가 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return

                    if(result.status == 1) {
                        MyApplication.bidx = result.bidx
                    }else
                        Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "결제 설정 최초 진입 시 row 추가 오류 >> $t")
                    Log.d(TAG, "결제 설정 최초 진입 시 row 추가 오류 >> ${call.request()}")
                }
            })
    }
}