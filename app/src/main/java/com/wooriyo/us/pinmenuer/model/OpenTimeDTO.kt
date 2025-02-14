package com.wooriyo.us.pinmenuer.model

import java.io.Serializable

data class OpenTimeDTO (    //영업시간
    var buse : String,   // D: 설정 안함, Y : 매일 동일한 영업시간, N : 매일 다른 영업시간
    var starttm : String,
    var endtm : String,

    var mon_buse : String,
    var mon_starttm : String,
    var mon_endtm : String,

    var tue_buse : String,
    var tue_starttm : String,
    var tue_endtm : String,

    var wed_buse : String,
    var wed_starttm : String,
    var wed_endtm : String,

    var thu_buse : String,
    var thu_starttm : String,
    var thu_endtm : String,

    var fri_buse : String,
    var fri_starttm : String,
    var fri_endtm : String,

    var sat_buse : String,
    var sat_starttm : String,
    var sat_endtm : String,

    var sun_buse : String,
    var sun_starttm : String,
    var sun_endtm : String
):Serializable {
    constructor() : this(
        "Y","","","N","","","N","","","N","","",
        "N","","","N","","","N","","","N","","")
}
