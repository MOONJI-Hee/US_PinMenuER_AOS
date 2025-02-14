package com.wooriyo.us.pinmenuer.model

import com.google.gson.annotations.SerializedName

data class CallListDTO(
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("clist") var callList: ArrayList<CallHistoryDTO>
)