package com.wooriyo.pinmenuer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.wooriyo.pinmenuer.MyApplication.Companion.db
import com.wooriyo.pinmenuer.db.entity.Member
import com.wooriyo.pinmenuer.member.LoginActivity
import com.wooriyo.pinmenuer.model.MemberDTO
import com.wooriyo.pinmenuer.store.StoreListActivity
import com.wooriyo.pinmenuer.store.StoreSetActivity
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class StartActivity: AppCompatActivity() {
    val TAG = "StartActivity"
    var idx = 0
    var id = ""
    var pw = ""
    var token = ""
    var osvs = 0
    var appvs = ""
    var md = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resources.displayMetrics.density = MyApplication.density
        resources.displayMetrics.densityDpi = MyApplication.dpi
        resources.displayMetrics.scaledDensity = MyApplication.density
        setContentView(R.layout.activity_start)

        AppHelper.hideInset(this)
    }

    override fun onResume() {
        super.onResume()
        val mbrDTO = MyApplication.pref.getMbrDTO()

        if (mbrDTO == null) {
            startActivity(Intent(this@StartActivity, LoginActivity::class.java).also {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } else {
            id = mbrDTO.userid
            pw = MyApplication.pref.getPw().toString()
            token = MyApplication.pref.getToken().toString()
            osvs = MyApplication.osver
            appvs = MyApplication.appver
            md = MyApplication.md

            loginWithApi()
        }
    }

    fun loginWithApi()  {
        ApiClient.service.checkMbr(id, pw, token, "A", osvs, appvs, md)
            .enqueue(object: retrofit2.Callback<MemberDTO> {
                override fun onResponse(call: Call<MemberDTO>, response: Response<MemberDTO>) {
                    if(response.isSuccessful) {
                        if(response.body()?.status == 1) {
                            val memberDTO = response.body()
                            if(memberDTO != null) {
                                MyApplication.pref.setMbrDTO(memberDTO)
                            }
                            startActivity(Intent(this@StartActivity, StoreListActivity::class.java))
                        }else {
                            startActivity(Intent(this@StartActivity, LoginActivity::class.java).also {
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            })
                        }
                    }
                }
                override fun onFailure(call: Call<MemberDTO>, t: Throwable) {
                    Log.d(TAG, t.toString())
                    loginWithDB()
                }
            })
    }

    fun loginWithDB () {

    }

    fun findMbr (member: Member) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val selUser =  db.userDao().findMbr(idx)
//
//            if(selUser == null) {
//                db.userDao().insUser(user)
//            }else {
//                db.userDao().udtMbr(user)
//            }
//        }
    }
}