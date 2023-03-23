package com.wooriyo.pinmenuer.model

import com.google.gson.annotations.SerializedName

data class CallSetListDTO(
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("callList") var callList: ArrayList<CallSetDTO>
)
