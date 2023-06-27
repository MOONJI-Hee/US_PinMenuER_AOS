package com.wooriyo.pinmenuer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.wooriyo.pinmenuer.MyApplication.Companion.androidId
import com.wooriyo.pinmenuer.MyApplication.Companion.pref
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

class StartActivity: BaseActivity() {
    val TAG = "StartActivity"

    var id = ""
    var pw = ""
    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
    }

    override fun onResume() {
        super.onResume()
        val mbrDTO = pref.getMbrDTO()

        if (mbrDTO == null) {
            startActivity(Intent(this@StartActivity, LoginActivity::class.java).also {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } else {
            id = mbrDTO.userid
            pw = pref.getPw().toString()
            token = pref.getToken().toString()

            loginWithApi()
        }
    }

    fun loginWithApi()  {
        ApiClient.service.checkMbr(id, pw, token, MyApplication.os, MyApplication.osver, MyApplication.appver, MyApplication.md, androidId, 1)
            .enqueue(object: retrofit2.Callback<MemberDTO> {
                override fun onResponse(call: Call<MemberDTO>, response: Response<MemberDTO>) {
                    Log.d(TAG, "자동 로그인 url : $response")
                    if(response.isSuccessful) {
                        if(response.body()?.status == 1) {
                            val memberDTO = response.body()
                            if(memberDTO != null) {
                                pref.setMbrDTO(memberDTO)
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
                    Log.d(TAG, "자동 로그인 실패 : $t")
                    Log.d(TAG, "자동 로그인 실패 : ${call.request()}")
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