package com.wooriyo.pinmenuer.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SpcHolidayDTO(
    @SerializedName("idx") var idx: Int,
    @SerializedName("title") var title: String,
    @SerializedName("month") var month: String,
    @SerializedName("day") var day: String
): Serializable
