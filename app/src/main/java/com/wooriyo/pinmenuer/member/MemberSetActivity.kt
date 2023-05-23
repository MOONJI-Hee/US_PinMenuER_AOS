package com.wooriyo.pinmenuer.member

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.common.ConfirmDialog
import com.wooriyo.pinmenuer.databinding.ActivitySignUpBinding
import com.wooriyo.pinmenuer.model.MemberDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MemberSetActivity: BaseActivity(), View.OnClickListener {
    val TAG = "MemberSetActivity"
    val mActivity = this@MemberSetActivity
    lateinit var binding : ActivitySignUpBinding

    var memberDTO: MemberDTO? = null
    var userid : String = ""
    var arpayoId : String = ""

    var arpaLinked = false

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        AppHelper.hideKeyboard(this, currentFocus, ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        memberDTO = MyApplication.pref.getMbrDTO()
        if(memberDTO != null) {
            userid = memberDTO!!.userid
            arpayoId = memberDTO!!.arpayoid?:""
        }

        binding.tvId.text = userid
        if(arpayoId.isNotEmpty()) {
            binding.etArpayo.setText(arpayoId)
            arpaLinked = true
            binding.linkResult.text = getString(R.string.link_after)
            binding.linkResult.setTextColor(Color.parseColor("#FF6200"))
        }
        binding.title.text = getString(R.string.title_udt_mbr)
        binding.save.text = getString(R.string.udt_info)

        binding.etId.visibility = View.INVISIBLE
        binding.btnCheckId.visibility = View.INVISIBLE
        binding.checkResult.visibility = View.INVISIBLE

        binding.tvId.visibility = View.VISIBLE
        binding.llUdt.visibility = View.VISIBLE

        binding.etArpayo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.linkResult.text = ""
                if(arpaLinked) {
                    arpaLinked = false
                }
            }
        })

        binding.back.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.drop.setOnClickListener(this)
        binding.logout.setOnClickListener(this)
        binding.btnArpayo.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onClick(p0: View?) {
        when(p0) {
            binding.back -> finish()
            binding.save -> save()
            binding.btnArpayo -> regArpayoId()
            binding.drop -> {
                val onClickListener = View.OnClickListener { drop() }
                ConfirmDialog(getString(R.string.dialog_drop), getString(R.string.btn_confirm), onClickListener).show(supportFragmentManager, "DropDialog")
            }
            binding.logout -> {
                val onClickListener = View.OnClickListener {logout()}
                ConfirmDialog(getString(R.string.dialog_logout), getString(R.string.btn_confirm), onClickListener).show(supportFragmentManager, "LogoutDialog")
            }
        }
    }

    private fun save() {
        val pass : String = binding.etPwd.text.toString()
        arpayoId = binding.etArpayo.text.toString()

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

        ApiClient.service.udtMbr(useridx, pass, arpayoId)
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "회원정보 수정 url : $response")
                    if(response.body()?.status == 1) {
                        MyApplication.pref.setPw(pass)
                        memberDTO?.arpayoid = arpayoId
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

    // 알파요 아이디 연동
    fun regArpayoId () {
        arpayoId = binding.etArpayo.text.toString()

        if(arpayoId.isEmpty() || arpayoId == "") {
            Toast.makeText(mActivity, R.string.arpayo_id_hint, Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.service.checkArpayo(arpayoId)
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "알파요 아이디 연동 url : $response")
                    if (!response.isSuccessful) return

                    val result = response.body()
                    if(result != null) {
                        if (result.status == 1) {
                            arpaLinked = true
                            binding.linkResult.text = getString(R.string.link_after)
                            binding.linkResult.setTextColor(Color.parseColor("#FF6200"))
                        } else {
                            Toast.makeText(this@MemberSetActivity, result.msg, Toast.LENGTH_SHORT).show()
                            arpaLinked = false
                            binding.linkResult.text = getString(R.string.link_fail)
                            binding.linkResult.setTextColor(Color.parseColor("#5A5A5A"))
                        }
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(this@MemberSetActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "알파요 아이디 연동 실패 > $t")
                    Log.d(TAG, "알파요 아이디 연동 실패 > ${call.request()}")
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
        MyApplication.pref.logout()
        val intent = Intent(mActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}