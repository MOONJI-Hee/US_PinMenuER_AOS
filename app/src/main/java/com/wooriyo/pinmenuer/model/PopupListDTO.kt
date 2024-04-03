package com.wooriyo.pinmenuer.model

import com.google.gson.annotations.SerializedName
import com.wooriyo.pinmenuer.model.PopupDTO

data class PopupListDTO (
    @SerializedName("status") var status : Int,
    @SerializedName("msg") var msg : String,
    @SerializedName("popuplist") var popupList : ArrayList<PopupDTO>
)