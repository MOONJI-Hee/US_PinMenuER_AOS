package com.wooriyo.pinmenuer.listener

import com.wooriyo.pinmenuer.model.CallDTO

interface ItemClickListener {
    fun onItemClick(position:Int) {}
    fun onItemMove(fromPos: Int, toPos: Int) {}
    fun onCallClick(position: Int, data: CallDTO) {}
}