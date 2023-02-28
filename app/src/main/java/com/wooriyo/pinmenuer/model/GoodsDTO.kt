package com.wooriyo.pinmenuer.model

import com.google.gson.annotations.SerializedName
import java.io.File

data class GoodsDTO (
    @SerializedName("gidx") var idx: Int,
    @SerializedName("name") var name : String,
    @SerializedName("content") var content : String?="",
    @SerializedName("cooking_time") var cooking_time: Int,
    @SerializedName("price") var price: Int,
    @SerializedName("img1") var img1 : String?="",
    @SerializedName("img2") var img2 : String?="",
    @SerializedName("img3") var img3 : String?="",
    var file1 : File?,       // 메뉴 등록, 수정 시 미디어 저장을 위한 변수
    var file2 : File?,       // 메뉴 등록, 수정 시 이미지 저장을 위한 변수
    var file3 : File?,
    @SerializedName("adDisplay") var adDisplay : String,
    @SerializedName("icon") var icon: Int,                          // 메뉴 상태 > 1: None, 2: Hide, 3: Best, 4: Sold Out, 5: New
    @SerializedName("boption") var boption : String
) {
    constructor():this(0, "", "", 0, 0, "", "", "", null, null, null, "N", 1, "N")
}