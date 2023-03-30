package com.wooriyo.pinmenuer.model

import com.google.gson.annotations.SerializedName

data class ResultDTO(
    @SerializedName("status") var status : Int,
    @SerializedName("msg") var msg : String,
    @SerializedName("idx") var idx : Int
)