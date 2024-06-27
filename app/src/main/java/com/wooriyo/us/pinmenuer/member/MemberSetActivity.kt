package com.wooriyo.us.pinmenuer.member

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wooriyo.us.pinmenuer.BaseActivity
import com.wooriyo.us.pinmenuer.MyApplication
import com.wooriyo.us.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.common.ConfirmDialog
import com.wooriyo.us.pinmenuer.databinding.ActivitySignUpBinding
import com.wooriyo.us.pinmenuer.model.MemberDTO
import com.wooriyo.us.pinmenuer.model.ResultDTO
import com.wooriyo.us.pinmenuer.util.ApiClient
import com.wooriyo.us.pinmenuer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MemberSetActivity: BaseActivity(), View.OnClickListener {
    lateinit var binding : ActivitySignUpBinding

    var memberDTO: MemberDTO? = null
    var userid : String = ""
    var arpayoId : String = ""

    var arpaLinked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        memberDTO = MyApplication.pref.getMbrDTO()
        if(memberDTO != null) {
            userid = memberDTO!!.userid
            arpayoId = memberDTO!!.arpayoid?:""
        }

        binding.title.text = getString(R.string.title_udt_mbr)
        binding.tvId.text = userid
        binding.etPwd.setText(MyApplication.pref.getPw())

        binding.etId.visibility = View.INVISIBLE
        binding.btnCheckId.visibility = View.INVISIBLE
        binding.checkResult.visibility = View.INVISIBLE

        binding.save.visibility = View.GONE
        binding.clTerms.visibility = View.GONE
        binding.signupInfo1.visibility = View.GONE
        binding.tvId.visibility = View.VISIBLE
        binding.modify.visibility = View.VISIBLE
        binding.llUdt.visibility = View.VISIBLE

        binding.back.setOnClickListener(this)
        binding.modify.setOnClickListener(this)
        binding.drop.setOnClickListener(this)
        binding.logout.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.back -> finish()
            binding.modify -> save()
            binding.drop -> {
                val onClickListener = View.OnClickListener { drop() }
                ConfirmDialog(getString(R.string.dialog_drop), "", getString(R.string.drop), onClickListener).show(supportFragmentManager, "DropDialog")
            }
            binding.logout -> {
                val onClickListener = View.OnClickListener {logout()}
                ConfirmDialog("", getString(R.string.dialog_logout), getString(R.string.confirm), onClickListener).show(supportFragmentManager, "LogoutDialog")
            }
        }
    }

    private fun save() {
        val pass : String = binding.etPwd.text.toString()

        if(pass.isEmpty() || pass == "") {
            Toast.makeText(mActivity, R.string.msg_no_pw, Toast.LENGTH_SHORT).show()
            return
        }else if(!AppHelper.verifyPw(pass)) {
            Toast.makeText(mActivity, R.string.msg_typemiss_pw, Toast.LENGTH_SHORT).show()
            return
        }else if ((arpayoId.isNotEmpty() && arpayoId != "") && !arpaLinked) {
            Toast.makeText(mActivity, R.string.msg_no_linked, Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.service.udtMbr(useridx, pass)
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "회원정보 수정 url : $response")
                    if(response.body()?.status == 1) {
                        MyApplication.pref.setPw(pass)
                        memberDTO?.let { MyApplication.pref.setMbrDTO(it) }
                        Toast.makeText(this@MemberSetActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        finish()
                    } else
                        Toast.makeText(this@MemberSetActivity, response.body()?.msg, Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(this@MemberSetActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "회원정보 수정 오류 > $t")
                }
            })
    }

    // 회원 탈퇴
    fun drop() {
        ApiClient.service.dropMbr(MyApplication.useridx).enqueue(object : Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "회원 탈퇴 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        logout()
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "회원 탈퇴 실패 >> $t")
                Log.d(TAG, "회원 탈퇴 실패 >> ${call.request()}")
            }
        })
    }

    fun logout() {
        ApiClient.service.logout(useridx, MyApplication.pref.getToken().toString()).enqueue(object : Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "로그아웃 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status) {
                    1 -> {
                        MyApplication.pref.logout()
                        val intent = Intent(mActivity, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "로그아웃 실패 >> $t")
                Log.d(TAG, "로그아웃 실패 >> ${call.request()}")
            }
        })
    }
}