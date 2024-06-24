package com.wooriyo.us.pinmenuer.listener

interface ItemTouchHelperListener {
    fun onItemMove(fromPos: Int, toPos: Int): Boolean
}