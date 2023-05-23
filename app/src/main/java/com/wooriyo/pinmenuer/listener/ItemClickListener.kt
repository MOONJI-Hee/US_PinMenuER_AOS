package com.wooriyo.pinmenuer.listener

import android.content.Intent
import com.wooriyo.pinmenuer.model.CallDTO
import com.wooriyo.pinmenuer.model.StoreDTO

interface ItemClickListener {
    fun onItemClick(position:Int) {}
    fun onItemMove(fromPos: Int, toPos: Int) {}
    fun onStoreClick(storeDTO: StoreDTO, intent: Intent) {}
    fun onCallClick(position: Int, data: CallDTO) {}
}