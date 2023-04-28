package com.wooriyo.pinmenuer.menu.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.databinding.ListValueBinding
import com.wooriyo.pinmenuer.model.OptionDTO

class ValueAdapter(val opt: OptionDTO): RecyclerView.Adapter<ValueAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListValueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(opt)
    }

    override fun getItemCount(): Int {
        return opt.optval.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    class ViewHolder(val binding:ListValueBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(opt: OptionDTO){
            val price = opt.optpay[absoluteAdapterPosition]

            if(price.isEmpty() || price == "0")
                binding.extra.visibility = View.GONE
            else {
                binding.extra.visibility = View.VISIBLE
                binding.mark.text = opt.optmark[absoluteAdapterPosition]
                binding.price.text = price
            }
            binding.value.text = opt.optval[absoluteAdapterPosition]
        }
    }
}