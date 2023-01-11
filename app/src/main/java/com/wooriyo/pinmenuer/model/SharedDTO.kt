package com.wooriyo.pinmenuer.model

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class SharedDTO(context: Context) {
    private  val pref : SharedPreferences = context.getSharedPreferences("PinMenuER_Pref_key", Context.MODE_PRIVATE)

    fun getMbrDTO(): MemberDTO? {
        val gson = Gson()
        val json: String ?= pref.getString("mbrData", "")
        return gson.fromJson(json, MemberDTO::class.java)
    }

    fun setMbrDTO(mbrData: MemberDTO) {
        val gson = Gson()
        val json = gson.toJson(mbrData)
        pref.edit().putString("mbrData", json).apply()
    }

    fun getUserIdx(): Int {
        return pref.getInt("useridx", 0)
    }

    fun setUserIdx(useridx: Int) {
        pref.edit().putInt("useridx", useridx).apply()
    }

    fun getPw() : String? {
        return pref.getString("pw", "")
    }

    fun setPw(pw : String) {
        pref.edit().putString("pw", pw).apply()
    }

    fun getToken(): String? {
        return pref.getString("push_token", "")
    }

    fun setToken(push_token: String) {
        pref.edit().putString("push_token", push_token).apply()
    }

}