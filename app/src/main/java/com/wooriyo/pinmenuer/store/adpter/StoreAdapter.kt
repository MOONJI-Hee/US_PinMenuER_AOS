package com.wooriyo.pinmenuer.store.adpter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.config.AppProperties.Companion.VIEW_TYPE_ADD
import com.wooriyo.pinmenuer.config.AppProperties.Companion.VIEW_TYPE_COM
import com.wooriyo.pinmenuer.databinding.ListStoreAddBinding
import com.wooriyo.pinmenuer.databinding.ListStoreBinding
import com.wooriyo.pinmenuer.model.StoreDTO
import com.wooriyo.pinmenuer.store.StoreMenuActivity
import com.wooriyo.pinmenuer.store.StoreSetActivity

class StoreAdapter(val dataSet: ArrayList<StoreDTO>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val binding_add = ListStoreAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return if(viewType == VIEW_TYPE_ADD) AddViewHolder(binding_add) else ViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == VIEW_TYPE_COM)
            (holder as ViewHolder).bind(dataSet[position])
        else
            (holder as AddViewHolder).bind()
    }

    override fun getItemCount(): Int {
        return dataSet.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == itemCount -1) VIEW_TYPE_ADD else VIEW_TYPE_COM
    }

    class ViewHolder(val context: Context, val binding: ListStoreBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoreDTO) {
            binding.run {
                storeName.text = data.name
                // TODO PAY ITEM 넣기
                if(data.paydt.isEmpty()) {
                    storePayInfo.visibility = View.INVISIBLE
                    storePayNo.visibility = View.VISIBLE
                }else{
                    storePayInfo.visibility = View.VISIBLE
                    storePayNo.visibility = View.GONE
                }

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

    class AddViewHolder(val binding: ListStoreAddBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind() {

        }
    }
}