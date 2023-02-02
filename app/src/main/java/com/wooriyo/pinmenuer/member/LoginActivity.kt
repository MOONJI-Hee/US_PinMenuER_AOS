package com.wooriyo.pinmenuer.member

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityLoginBinding
import com.wooriyo.pinmenuer.menu.CategorySetActivity
import com.wooriyo.pinmenuer.model.MemberDTO
import com.wooriyo.pinmenuer.store.StoreListActivity
import com.wooriyo.pinmenuer.store.StoreSetActivity
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import com.wooriyo.pinmenuer.util.Encoder
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    val TAG = "LoginActivity"
    var waitTime = 0L

    var id = ""
    var pw = ""
    var token = ""
    var osvs = 0
    var appvs = ""
    var md = ""

    // 뒤로가기 눌렀을 때 처리
    override fun onBackPressed() {
        if(System.currentTimeMillis() - waitTime > 2500) {
            waitTime = System.currentTimeMillis()
            Toast.makeText(this@LoginActivity, R.string.backpress, Toast.LENGTH_LONG).show()
        } else {
            finishAffinity()
        }
    }
    // 바깥화면 터치하면 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        AppHelper.hideKeyboard(this, currentFocus, ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener{
            id = binding.etId.text.toString()
            pw = binding.etPwd.text.toString()
            token = MyApplication.pref.getToken().toString()
            osvs = MyApplication.osver
            appvs = MyApplication.appver
            md = MyApplication.md

            startActivity(Intent(this@LoginActivity, CategorySetActivity::class.java))

//            when {
//                id.isEmpty() -> Toast.makeText(this@LoginActivity, R.string.msg_no_id, Toast.LENGTH_SHORT).show()
//                pw.isEmpty() -> Toast.makeText(this@LoginActivity, R.string.msg_no_pw, Toast.LENGTH_SHORT).show()
//                else -> loginWithApi()
//            }
        }

        binding.signup.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
    }

    private fun loginWithApi()  {
        ApiClient.service.checkMbr(Encoder.encodeUTF8(id), Encoder.encodeUTF8(pw), token, "A", osvs, appvs, md)
            .enqueue(object: retrofit2.Callback<MemberDTO> {
                override fun onResponse(call: Call<MemberDTO>, response: Response<MemberDTO>) {
                    Log.d(TAG, "로그인 url : $response")
                    if(response.isSuccessful) {
                        val memberDTO = response.body()
                        if(memberDTO != null) {
                            if(memberDTO.status == 1) {
                                MyApplication.pref.setMbrDTO(memberDTO)
                                Toast.makeText(this@LoginActivity, memberDTO.msg, Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity, StoreListActivity::class.java))
                            } else {
                                Toast.makeText(this@LoginActivity, memberDTO.msg, Toast.LENGTH_SHORT).show()
                            }
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