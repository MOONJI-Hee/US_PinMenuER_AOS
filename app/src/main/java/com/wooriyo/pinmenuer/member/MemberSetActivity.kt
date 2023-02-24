package com.wooriyo.pinmenuer.member

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

class MemberSetActivity: BaseActivity(), View.OnClickListener {
    val TAG = "MemberSetActivity"
    lateinit var binding : ActivitySignUpBinding

    var memberDTO: MemberDTO? = null
    var userid : String = ""
    var arpayoId : String = ""

    var useridx : Int = 0

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
        }
        binding.title.text = getString(R.string.title_udt_mbr)
        binding.save.text = getString(R.string.udt_info)

        binding.etId.visibility = View.INVISIBLE
        binding.btnCheckId.visibility = View.INVISIBLE
        binding.checkResult.visibility = View.INVISIBLE
        binding.tvId.visibility = View.VISIBLE

        binding.back.setOnClickListener(this)
        binding.save.setOnClickListener(this)
        binding.btnArpayo.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        AppHelper.hideInset(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            binding.back.id -> finish()
            binding.save.id -> save()
            binding.btnArpayo.id -> regArpayoId()
        }
    }

    private fun save() {
        val pass : String = binding.etPwd.text.toString()
        arpayoId = binding.etArpayo.text.toString()
        ApiClient.service.udtMbr(useridx, pass, arpayoId)
            .enqueue(object : retrofit2.Callback<ResultDTO> {
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

    fun regArpayoId () {
        arpayoId = binding.etArpayo.text.toString()
        ApiClient.service.checkArpayo(arpayoId)
            .enqueue(object : Callback<ResultDTO> {
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "알파요 아이디 연동 url : $response")
                    if (response.isSuccessful) {
                        if (response.body()?.status == 1) {
                            binding.linkResult.text = getString(R.string.link_after)
                            binding.linkResult.setTextColor(Color.parseColor("#FF6200"))
                        } else {

                        }
                    }
                }
                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(this@MemberSetActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "알파요 아이디 연동 실패 > $t")
                }
            })
    }
}