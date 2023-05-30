package com.wooriyo.pinmenuer.model

import com.google.gson.annotations.SerializedName

data class OrderListDTO(
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("totalCnt") var totalCnt: Int,
    @SerializedName("orderlist") var orderlist: ArrayList<OrderHistoryDTO>
)