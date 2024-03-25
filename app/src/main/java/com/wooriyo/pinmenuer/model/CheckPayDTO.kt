package com.wooriyo.pinmenuer.model

import com.google.gson.annotations.SerializedName

data class CheckPayDTO(
    @SerializedName("idx") var idx : Int,
    @SerializedName("name") var name : String,
    @SerializedName("payuse") var payuse : String
)
