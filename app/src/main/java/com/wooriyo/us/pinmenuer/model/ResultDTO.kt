package com.wooriyo.us.pinmenuer.model

import com.google.gson.annotations.SerializedName

data class ResultDTO(
    @SerializedName("status") var status : Int,
    @SerializedName("msg") var msg : String,
    @SerializedName("idx") var idx : Int,

    @SerializedName("review") var review : Int,
    @SerializedName("curver") var curver : String,          //최신 버전
    @SerializedName("update") var update : Int,             //강제 업데이트 설정값 (0:권장 업데이트, 1:강제 업데이트)
    @SerializedName("updatemsg") var updateMsg : String,    //업데이트 메세지

    @SerializedName("checklist") var checklist : ArrayList<CheckPayDTO>,    //요금제 만료 여부 리스트

    @SerializedName("bidx") var bidx : Int,
    @SerializedName("photo") var img1 : String?="",
    @SerializedName("photo2") var img2 : String?="",
    @SerializedName("photo3") var img3 : String?="",
    @SerializedName("stropt") var opt : ArrayList<OptionDTO>?,

    @SerializedName("qidx") var qidx : Int,
    @SerializedName("filepath") var filePath: String
)