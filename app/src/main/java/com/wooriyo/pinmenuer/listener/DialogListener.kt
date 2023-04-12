package com.wooriyo.pinmenuer.listener

import com.wooriyo.pinmenuer.model.CallDTO
import com.wooriyo.pinmenuer.model.SpcHolidayDTO

interface DialogListener {
    fun onTimeSet(start: String, end: String) {}
    fun onHolidaySet(position: Int, data: SpcHolidayDTO) {}
    fun onCallSet(position: Int, data: CallDTO) {}
    fun onItemDelete(position: Int) {}
}