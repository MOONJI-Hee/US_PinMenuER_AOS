package com.wooriyo.pinmenuer.model

import com.google.gson.annotations.SerializedName
import com.wooriyo.pinmenuer.model.TableNoDTO

data class TableNoListDTO(
    @SerializedName("status") var status : Int,
    @SerializedName("msg") var msg : String,
    @SerializedName("tableNoList") var tableNoList : ArrayList<TableNoDTO>
)
