package com.wooriyo.us.pinmenuer.listener

import android.content.Intent
import android.widget.CheckBox
import com.wooriyo.us.pinmenuer.model.CallDTO
import com.wooriyo.us.pinmenuer.model.StoreDTO

interface ItemClickListener {
    fun onItemClick(position:Int) {}
    fun onItemMove(fromPos: Int, toPos: Int) {}
    fun onQrClick(position: Int, status: Boolean) {}
    fun onStoreClick(storeDTO: StoreDTO, intent: Intent, usePay: Boolean) {}
    fun onCallClick(position: Int, data: CallDTO) {}
    fun onCheckClick(position: Int, v: CheckBox, isChecked : Boolean) {}
}