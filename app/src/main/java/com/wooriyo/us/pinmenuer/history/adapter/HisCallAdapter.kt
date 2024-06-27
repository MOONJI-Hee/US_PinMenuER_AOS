package com.wooriyo.us.pinmenuer.history.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.us.pinmenuer.databinding.ListCallDetailBinding
import com.wooriyo.us.pinmenuer.model.OrderDTO

class HisCallAdapter(val dataSet: ArrayList<OrderDTO>): RecyclerView.Adapter<HisCallAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListCallDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListCallDetailBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind (data : OrderDTO) {
            binding.run {
                name.text = data.name
                if(data.gea > 0) {
                    amount.text = data.gea.toString()
                    tvGea.visibility = View.VISIBLE
                }else {
                    amount.text = ""
                    tvGea.visibility = View.GONE
                }
            }
        }
    }
}