package com.wooriyo.us.pinmenuer.model

import com.google.gson.annotations.SerializedName

data class OrderOptDTO(
    @SerializedName("name") var name : String,
    @SerializedName("price") var price : String
)