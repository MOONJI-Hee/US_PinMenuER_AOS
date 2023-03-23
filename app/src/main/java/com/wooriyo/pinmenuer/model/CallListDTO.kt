package com.wooriyo.pinmenuer.model

import com.google.gson.annotations.SerializedName

data class CallListDTO(
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("callList") var callList: ArrayList<CallSetDTO>,
    var tableNo: String,
    var regdt: String
)
