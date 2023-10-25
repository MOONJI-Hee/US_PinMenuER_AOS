package com.wooriyo.pinmenuer.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.databinding.ListOrderDetailBinding
import com.wooriyo.pinmenuer.model.OrderDTO
import com.wooriyo.pinmenuer.util.AppHelper
import com.wooriyo.pinmenuer.util.AppHelper.Companion.setHeight

class OrderDetailAdapter(val dataSet: ArrayList<OrderDTO>): RecyclerView.Adapter<OrderDetailAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListOrderDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListOrderDetailBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind (data : OrderDTO) {
            binding.run {
                name.text = data.name
                gea.text = data.gea.toString()
                price.text = AppHelper.price(data.price)

                if(data.opt.isNotEmpty()) {
                    var strOpt = ""

                    data.opt.forEach {
                        if(strOpt == "")
                            strOpt = it
                        else
                            strOpt += "\n$it"
                    }

                    var height = 100

                    val multiple = (data.opt.size - 1) / 4

                    height += (104 * multiple)

                    setHeight(layout, height)

                    option.text = strOpt
                    option.visibility = View.VISIBLE
                }else{
                    option.visibility = View.GONE
                    setHeight(layout, 100)
                }
            }
        }
    }
}