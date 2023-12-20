package com.wooriyo.pinmenuer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenuer.MyApplication.Companion.androidId
import com.wooriyo.pinmenuer.MyApplication.Companion.appver
import com.wooriyo.pinmenuer.MyApplication.Companion.pref
import com.wooriyo.pinmenuer.common.UpdateDialog
import com.wooriyo.pinmenuer.db.entity.Member
import com.wooriyo.pinmenuer.member.LoginActivity
import com.wooriyo.pinmenuer.model.MemberDTO
import com.wooriyo.pinmenuer.model.ResultDTO
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
    var id = ""
    var pw = ""
    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
    }

    override fun onResume() {
        super.onResume()
        checkVersion()
    }

    fun getMbrInfo() {
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

    fun loginWithApi() {
        ApiClient.service.checkMbr(id, pw, token, MyApplication.os, MyApplication.osver, MyApplication.appver, MyApplication.md, androidId, 1)
            .enqueue(object : retrofit2.Callback<MemberDTO> {
                override fun onResponse(call: Call<MemberDTO>, response: Response<MemberDTO>) {
                    Log.d(TAG, "자동 로그인 url : $response")
                    if (response.isSuccessful) {
                        if (response.body()?.status == 1) {
                            val memberDTO = response.body()
                            if (memberDTO != null) {
                                pref.setMbrDTO(memberDTO)
                            }
                            startActivity(Intent(this@StartActivity, StoreListActivity::class.java))
                        } else {
                            startActivity(
                                Intent(
                                    this@StartActivity,
                                    LoginActivity::class.java
                                ).also {
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
//                    loginWithDB()
                }
            })
    }

    fun checkVersion() {
        ApiClient.service.checkVersion(1, appver, 2)
            .enqueue(object : retrofit2.Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "버전 확인 url : $response")
                    if (!response.isSuccessful) return

                    val result = response.body() ?: return

                    if (result.status == 1) {
                        val curver = result.curver
                        if (AppHelper.compareVer(curver)) {  // 최신버전 이상
                            getMbrInfo()
                        } else { // 최신버전 이하
                            val dialog = UpdateDialog(result.update, result.updateMsg)
                            dialog.setCancelClickListener { getMbrInfo() }
                            dialog.show(supportFragmentManager, "UpdateDialog")
                        }
                    } else {
                        Toast.makeText(this@StartActivity, result.msg, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "버전 확인 status != 1")
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Log.d(TAG, "버전 확인 실패 >> $t")
                }
            })
    }
}