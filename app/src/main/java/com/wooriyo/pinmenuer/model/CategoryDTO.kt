package com.wooriyo.pinmenuer.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CategoryDTO (
    @SerializedName("idx") var idx : Int,
    @SerializedName("pidx") var pidx : Int,
    @SerializedName("name") var name : String,
    @SerializedName("subname") var subname : String,
    @SerializedName("code") var code : String,
    @SerializedName("seq") var seq : Int,
    @SerializedName("buse") var buse : String
): Serializable