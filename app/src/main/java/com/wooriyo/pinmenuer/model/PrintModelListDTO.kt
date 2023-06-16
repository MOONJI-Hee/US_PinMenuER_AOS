package com.wooriyo.pinmenuer.model

import com.google.gson.annotations.SerializedName

data class PrintModelListDTO(
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("printList") var printList: ArrayList<PrintModelDTO>
)
