package com.wooriyo.pinmenuer.listener

import android.content.Intent
import android.widget.CheckBox
import com.wooriyo.pinmenuer.model.CallDTO
import com.wooriyo.pinmenuer.model.PrintDTO
import com.wooriyo.pinmenuer.model.StoreDTO

interface ItemClickListener {
    fun onItemClick(position:Int) {}
    fun onItemMove(fromPos: Int, toPos: Int) {}
    fun onQrClick(position: Int, status: Boolean) {}
    fun onStoreClick(storeDTO: StoreDTO, intent: Intent) {}
    fun onCallClick(position: Int, data: CallDTO) {}
    fun onCheckClick(position: Int, v: CheckBox, isChecked : Boolean) {}
}