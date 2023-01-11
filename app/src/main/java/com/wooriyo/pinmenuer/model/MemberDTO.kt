package com.wooriyo.pinmenuer.model

data class MemberDTO (
    var status : Int,
    var msg : String,
    var useridx : Int,
    var userid : String,
    var name : String,
    var arpayoid: String ? = "",
    var use_start : String ? = "",
    var use_end : String ?= ""
)