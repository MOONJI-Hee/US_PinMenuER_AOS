package com.wooriyo.pinmenuer.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StoreDTO(
    @SerializedName("idx") var idx : Int,
    @SerializedName("pidx") var useridx : Int,
    @SerializedName("name") var name : String,
    @SerializedName("buse") var buse : String,
    @SerializedName("paydt") var paydt : String,
    @SerializedName("address") var address : String,
    @SerializedName("zipcode") var zipcode : String,
    @SerializedName("img") var img : String,
    @SerializedName("status") var status : String,
    @SerializedName("statusdt") var statusdt : String,
):Serializable {
    constructor(useridx: Int) :  this(0, useridx, "", "", "", "", "", "", "", "")
}
