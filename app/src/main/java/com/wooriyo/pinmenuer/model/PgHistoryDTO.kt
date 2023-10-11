package com.wooriyo.pinmenuer.model

import com.google.gson.annotations.SerializedName
import com.wooriyo.pinmenuer.model.PgDetailDTO

data class PgHistoryDTO(
    @SerializedName("title") var title: String,
    @SerializedName("glist") var pgDetailList: ArrayList<PgDetailDTO>
)