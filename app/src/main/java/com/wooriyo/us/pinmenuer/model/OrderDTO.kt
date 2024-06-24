package com.wooriyo.us.pinmenuer.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OrderDTO(
    @SerializedName("idx") var idx : Int,               // 상품 idx
    @SerializedName("name") var name: String,           // 상품 이름
    @SerializedName("gea") var gea : Int,               // 상품 개수
    @SerializedName("price") var price : Int,           // 상품 가격 (하나당 가격)
    @SerializedName("amount") var amount : Int,         // 총 금액 ( 상품 가격 * 상품 개수)
    @SerializedName("opt") var opt: ArrayList<String>,  // 추가 옵션 ( val1, val2, ... )
    @SerializedName("ispos") var ispos: Int,            // 포스 연동 여부 (0: 포스 사용 X, 2: 포스 연동 안됨)
    @SerializedName("togotype") var togotype: Int,      // 0 : 아무것도 없음,  1:신규 , 2: 포장
    var isChecked: Boolean = true
): Serializable