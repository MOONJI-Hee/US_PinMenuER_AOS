package com.wooriyo.pinmenuer.model

import com.google.gson.annotations.SerializedName

data class ResultDTO(
    @SerializedName("status") var status : Int,
    @SerializedName("msg") var msg : String,
    @SerializedName("idx") var idx : Int,
    @SerializedName("bidx") var bidx : Int,
    @SerializedName("photo") var img1 : String?="",
    @SerializedName("photo2") var img2 : String?="",
    @SerializedName("photo3") var img3 : String?=""
)