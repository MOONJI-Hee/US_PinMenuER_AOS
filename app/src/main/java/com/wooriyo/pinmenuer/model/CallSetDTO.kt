package com.wooriyo.pinmenuer.model

data class CallSetDTO(
    var idx: Int,
    var pidx: Int,
    var name: String,
    var seq : Int
){
    constructor(): this(0, 0, "", 0)
}
