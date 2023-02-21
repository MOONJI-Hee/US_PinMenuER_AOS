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
    @SerializedName("lat") var lat : String,
    @SerializedName("long") var long : String,
    @SerializedName("img") var img : String,
    @SerializedName("regdt") var regdt : String,
    @SerializedName("status") var status : String,
    @SerializedName("statusdt") var statusdt : String,
    @SerializedName("tel") var tel : String?,
    @SerializedName("sns") var sns : String?,
    @SerializedName("delivery") var delivery : String?="N",
    @SerializedName("parking") var parking : String?="N",
    @SerializedName("parkingadr") var parkingAddr : String?,
    @SerializedName("hbuse") var hbuse : String,
    @SerializedName("mon_buse") var mon_buse : String,
    @SerializedName("tue_buse") var tue_buse : String,
    @SerializedName("wed_buse") var wed_buse : String,
    @SerializedName("thu_buse") var thu_buse : String,
    @SerializedName("fri_buse") var fri_buse : String,
    @SerializedName("sat_buse") var sat_buse : String,
    @SerializedName("sun_buse") var sun_buse : String,
    @SerializedName("workList")  var opentime: OpenTimeDTO?,
    @SerializedName("breakList")  var breaktime: BrkTimeDTO?,
    @SerializedName("holidayList")  var spcHoliday: ArrayList<SpcHolidayDTO>?
):Serializable {
    constructor(useridx: Int) :  this(0, useridx, "", "", "", "", "", "", "", "", "", "", "", null, null, null, null, null,
        "N", "N", "N", "N", "N", "N", "N", "N" ,null, null, null)
}
