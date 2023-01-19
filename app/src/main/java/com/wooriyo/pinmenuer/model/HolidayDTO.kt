package com.wooriyo.pinmenuer.model

data class HolidayDTO(
    var buse : String,   // Y : 매주 동일한 휴일, N : 매주 다른 휴일
    var mon_buse : String,
    var tue_buse : String,
    var wed_buse : String,
    var thu_buse : String,
    var fri_buse : String,
    var sat_buse : String,
    var sun_buse : String
)
