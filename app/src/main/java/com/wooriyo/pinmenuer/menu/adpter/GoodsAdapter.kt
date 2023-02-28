package com.wooriyo.pinmenuer.menu.adpter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.databinding.ListMenuBinding
import com.wooriyo.pinmenuer.model.GoodsDTO
import com.wooriyo.pinmenuer.util.AppHelper

class GoodsAdapter(val dataSet: ArrayList<GoodsDTO>): RecyclerView.Adapter<GoodsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding:ListMenuBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: GoodsDTO) {
            binding.run {
                name.text = data.name
                price.text = AppHelper.price(data.price)
            }
        }

    }
}