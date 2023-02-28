package com.wooriyo.pinmenuer.model

data class OptionDTO (
    var name : String,
    var optval : ArrayList<String>,
    var optpay : ArrayList<String>?,
    var type : Int
) {
    constructor(type: Int) : this("", arrayListOf(""), null, type)
}