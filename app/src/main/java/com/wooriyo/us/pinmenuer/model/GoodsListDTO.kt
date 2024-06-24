package com.wooriyo.us.pinmenuer.model

import com.google.gson.annotations.SerializedName

data class GoodsListDTO(
    @SerializedName("status") var status : Int,
    @SerializedName("msg") var msg : String,
    @SerializedName("total") var total : Int,
    @SerializedName("glist") var glist : ArrayList<GoodsDTO>
)
