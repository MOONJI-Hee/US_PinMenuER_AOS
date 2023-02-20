package com.wooriyo.pinmenuer.model

import com.google.gson.annotations.SerializedName

data class CallSetListDTO(
    var status: Int,
    var msg: String,
    @SerializedName("callList") var callList: ArrayList<CallSetDTO>
)
