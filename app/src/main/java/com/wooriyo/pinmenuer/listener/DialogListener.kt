package com.wooriyo.pinmenuer.listener

import com.wooriyo.pinmenuer.model.CallDTO
import com.wooriyo.pinmenuer.model.CategoryDTO
import com.wooriyo.pinmenuer.model.OptionDTO
import com.wooriyo.pinmenuer.model.SpcHolidayDTO

interface DialogListener {
    fun onConfirm() {}
    fun onTimeSet(start: String, end: String) {}
    fun onHolidaySet(position: Int, data: SpcHolidayDTO) {}
    fun onCallSet(position: Int, data: CallDTO) {}
    fun onCateAdd(cateList: ArrayList<CategoryDTO>) {}
    fun onCateSet(position: Int, data: CategoryDTO) {}
    fun onOptAdd(option: OptionDTO) {}
    fun onOptSet(position: Int, option: OptionDTO) {}
    fun onItemDelete(position: Int) {}
    fun onNickSet(nick: String) {}
    fun onComplete(popup: Int) {}
}