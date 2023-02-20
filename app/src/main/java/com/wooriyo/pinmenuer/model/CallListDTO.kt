package com.wooriyo.pinmenuer.model

import com.google.gson.annotations.SerializedName

data class CallListDTO(
    var status: Int,
    var msg: String,
    var tableNo: String,
    @SerializedName("callList") var callList: ArrayList<CallDTO>,
    var regdt: String
)
