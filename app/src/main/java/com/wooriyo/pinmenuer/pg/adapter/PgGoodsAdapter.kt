package com.wooriyo.pinmenuer.pg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.databinding.ListPgGoodsBinding
import com.wooriyo.pinmenuer.model.PgDetailDTO
import com.wooriyo.pinmenuer.util.AppHelper

class PgGoodsAdapter(val dataSet: ArrayList<PgDetailDTO>): RecyclerView.Adapter<PgGoodsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListPgGoodsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListPgGoodsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PgDetailDTO) {
            binding.run {
                name.text = data.name
                cnt.text = data.gea.toString()
                price.text = AppHelper.price(data.price)

                //TODO option
            }
        }
    }
}