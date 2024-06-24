package com.wooriyo.us.pinmenuer.menu.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.databinding.ListValueBinding
import com.wooriyo.us.pinmenuer.model.ValueDTO
import com.wooriyo.us.pinmenuer.util.AppHelper

class ValueAdapter(val dataSet: ArrayList<ValueDTO>): RecyclerView.Adapter<ValueAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListValueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getItemId(position: Int): Long {
        return dataSet[position].idx.toLong()
    }

    class ViewHolder(val binding:ListValueBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ValueDTO){
            val price = data.price

            if(price.isEmpty() || price == "0")
                binding.extra.visibility = View.GONE
            else {
                binding.extra.visibility = View.VISIBLE
                binding.mark.text = data.mark
                binding.price.text = AppHelper.price(price.toInt())
            }
            binding.value.text = data.name
        }
    }
}