package com.wooriyo.pinmenuer.listener

interface ItemTouchHelperListener {
    fun onItemMove(fromPos: Int, toPos: Int): Boolean
}