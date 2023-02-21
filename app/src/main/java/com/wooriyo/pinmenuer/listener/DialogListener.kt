package com.wooriyo.pinmenuer.listener

import com.wooriyo.pinmenuer.model.SpcHolidayDTO

interface DialogListener {
    fun onTimeSet(start: String, end: String) {}
    fun onHolidaySet(data: SpcHolidayDTO) {}
}