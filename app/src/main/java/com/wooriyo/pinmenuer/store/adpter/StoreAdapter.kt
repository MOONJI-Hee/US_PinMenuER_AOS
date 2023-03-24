package com.wooriyo.pinmenuer.store.adpter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ListStoreBinding
import com.wooriyo.pinmenuer.model.StoreDTO
import com.wooriyo.pinmenuer.store.StoreMenuActivity
import com.wooriyo.pinmenuer.store.StoreSetActivity

class StoreAdapter(val dataSet: ArrayList<StoreDTO>): RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val context: Context, val binding: ListStoreBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoreDTO) {
            binding.run {
                storeName.text = data.name
                // TODO PAY ITEM 넣기
                if(data.paydt.isEmpty())
                    storePayInfo.text = context.getText(R.string.store_pay_no)
                else
                    storePayInfo.text = context.getText(R.string.store_pay_date)

                storePayDt.text = data.paydt.split(" ")[0].replace("-", ".")

                storeName.setOnClickListener {
                    MyApplication.storeidx = data.idx
                    val intent = Intent(context, StoreMenuActivity::class.java)
                    intent.putExtra("store", data)
                    context.startActivity(intent)
                }

                storeUdt.setOnClickListener{
                    val intent = Intent(context, StoreSetActivity::class.java)
                    intent.putExtra("store", data)
                    intent.putExtra("type", 2)  // 매장 수정 > 2
                    context.startActivity(intent)
                }
            }
        }
    }
}