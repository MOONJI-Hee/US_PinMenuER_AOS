package com.wooriyo.us.pinmenuer.model

import com.google.gson.annotations.SerializedName

data class OptionDTO (
    @SerializedName("opt_idx") var idx : Int,
    @SerializedName("opt_title") var title : String,
    @SerializedName("opt_req") var optreq : Int,               // 0 : 선택 옵션, 1 : 필수 옵션
    @SerializedName("optlist") var optval : ArrayList<ValueDTO>?
) {
    constructor(req: Int) : this(0, "", req, ArrayList<ValueDTO>())
    fun deepCopy(): OptionDTO {
        val copyList = ArrayList<ValueDTO>()
        optval?.forEach {
            copyList.add(it.copy())
        }
        return OptionDTO(idx, title, optreq, copyList)
    }
}