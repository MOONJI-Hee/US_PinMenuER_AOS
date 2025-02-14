package com.wooriyo.us.pinmenuer.member

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.us.pinmenuer.BaseActivity
import com.wooriyo.us.pinmenuer.MyApplication
import com.wooriyo.us.pinmenuer.MyApplication.Companion.androidId
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.databinding.ActivityLoginBinding
import com.wooriyo.us.pinmenuer.model.MemberDTO
import com.wooriyo.us.pinmenuer.store.StoreListActivity
import com.wooriyo.us.pinmenuer.util.ApiClient
import com.wooriyo.us.pinmenuer.util.AppHelper.Companion.verifyEmail
import com.wooriyo.us.pinmenuer.util.AppHelper.Companion.verifyPw
import retrofit2.Call
import retrofit2.Response

class LoginActivity : BaseActivity() {
    lateinit var binding: ActivityLoginBinding

    var waitTime = 0L
    var id = ""
    var pw = ""
    var token = ""

    // 뒤로가기 눌렀을 때 처리
    override fun onBackPressed() {
        if(System.currentTimeMillis() - waitTime > 2500) {
            waitTime = System.currentTimeMillis()
            Toast.makeText(this@LoginActivity, R.string.backpress, Toast.LENGTH_LONG).show()
        } else {
            finishAffinity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.version.text = "Ver ${MyApplication.appver}"

        binding.login.setOnClickListener{       // 로그인 버튼 클릭
            id = binding.etId.text.toString()
            pw = binding.etPwd.text.toString()
            token = MyApplication.pref.getToken().toString()
            when {
                id.isEmpty() -> Toast.makeText(this@LoginActivity, R.string.msg_no_id, Toast.LENGTH_SHORT).show()
                pw.isEmpty() -> Toast.makeText(this@LoginActivity, R.string.msg_no_pw, Toast.LENGTH_SHORT).show()
                !verifyEmail(id) -> Toast.makeText(this@LoginActivity, R.string.msg_typemiss_id, Toast.LENGTH_SHORT).show()
                !verifyPw(pw) -> Toast.makeText(this@LoginActivity, R.string.msg_typemiss_pw, Toast.LENGTH_SHORT).show()
                else -> loginWithApi()
            }
        }

        binding.signup.setOnClickListener {     // 회원가입 버튼 클릭
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }

        binding.findPw.setOnClickListener {
            startActivity(Intent(this@LoginActivity, FindPwdActivity::class.java))
        }

        // master login
        binding.logo.setOnLongClickListener {
            startActivity(Intent(this@LoginActivity, MasterLoginActivity::class.java))
            return@setOnLongClickListener true
        }
    }

    private fun loginWithApi()  {   // Api로 로그인
        ApiClient.service.checkMbr(id, pw, token, MyApplication.os, MyApplication.osver, MyApplication.appver, MyApplication.md, androidId, 1)
            .enqueue(object: retrofit2.Callback<MemberDTO> {
                override fun onResponse(call: Call<MemberDTO>, response: Response<MemberDTO>) {
                    Log.d(TAG, "로그인 url : $response")
                    if(response.isSuccessful) {
                        val memberDTO = response.body()
                        if(memberDTO != null) {
                            Log.d(TAG, memberDTO.toString())
                            if(memberDTO.status == 1) {
                                MyApplication.pref.setMbrDTO(memberDTO)
                                MyApplication.pref.setUserIdx(memberDTO.useridx)
                                MyApplication.pref.setPw(pw)
                                Toast.makeText(this@LoginActivity, memberDTO.msg, Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity, StoreListActivity::class.java))
                            } else
                                Toast.makeText(this@LoginActivity, memberDTO.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<MemberDTO>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "api 로그인 실패 > $t")
//                    loginWithDB()
                }
            })
    }
}