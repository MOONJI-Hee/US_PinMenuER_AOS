package com.wooriyo.pinmenuer.member

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivitySignUpBinding
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Response

class SignUpActivity : AppCompatActivity(), View.OnClickListener {
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

        //        @Field("userid") userid: String,
        //        @Field("alpha_userid") arpayo_id: String,
        //        @Field("user_pwd") pw: String,
        //        @Field("push_token") push_token: String,
        //        @Field("os") os: String,
        //        @Field("osvs") osvs: String,
        //        @Field("appvs") appvs: String,
        //        @Field("md") md: String

        ApiClient.service.regMember(userid, arpayoId, pass, token, os, osvs, appvs, md)
    }
}


//  userid		회원아이디
//  user_pwd  비밀번호
//  alpha_userid				알파요 iD
//  osvs	OS버전(예:1.0.0)
//  appvs	어플버전
//  md		단말기모델명
//  os    인드로이드 A , 아이폰 I
//  push_token 푸시 토큰