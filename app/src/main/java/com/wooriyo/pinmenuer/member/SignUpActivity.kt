package com.wooriyo.pinmenuer.member

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivitySignUpBinding
import com.wooriyo.pinmenuer.model.MemberDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : BaseActivity(), View.OnClickListener {
    val TAG = "SignUpActivity"
    lateinit var binding : ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.btnCheckId.setOnClickListener(this)
        binding.btnArpayo.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            binding.back.id -> finish()
            binding.save.id -> save()
            binding.btnCheckId.id -> {
                //TODO 아이디 중복 체크
            }
            binding.btnArpayo.id -> {
                //TODO 알파요 아이디 연동
            }
        }
    }

    private fun save() {
        val userid : String = binding.etId.text.toString()
        val pass : String = binding.etPwd.text.toString()
        val arpayoId = binding.etArpayo.text.toString()
        val token = MyApplication.pref.getToken() ?: ""
        val os = "A"
        val osvs = MyApplication.osver
        val appvs = MyApplication.appver
        val md = MyApplication.md

        ApiClient.service.regMember(userid, arpayoId, pass, token, os, osvs, appvs, md)
            .enqueue(object : Callback<MemberDTO>{
                override fun onResponse(call: Call<MemberDTO>, response: Response<MemberDTO>) {
                    Log.d(TAG, "회원가입 url : $response")
                    if(response.isSuccessful) {
                        val memberDTO = response.body()
                        if(memberDTO != null) {
                            if(memberDTO.status == 1) {
                                Toast.makeText(this@SignUpActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                            }else {
                                Toast.makeText(this@SignUpActivity, memberDTO.msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<MemberDTO>, t: Throwable) {
                    Toast.makeText(this@SignUpActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "회원가입 오류 > $t")
                }
            })
    }
}