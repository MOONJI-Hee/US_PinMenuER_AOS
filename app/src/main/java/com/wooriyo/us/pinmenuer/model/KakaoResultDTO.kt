package com.wooriyo.us.pinmenuer.model

import com.google.gson.annotations.SerializedName

data class KakaoResultDTO (
    @SerializedName("documents") var documents: ArrayList<AddrDTO>
)