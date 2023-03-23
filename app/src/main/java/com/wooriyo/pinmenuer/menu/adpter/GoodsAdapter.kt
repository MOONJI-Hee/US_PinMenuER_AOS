package com.wooriyo.pinmenuer.menu.adpter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ListMenuBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.GoodsDTO
import com.wooriyo.pinmenuer.util.AppHelper

class GoodsAdapter(val dataSet: ArrayList<GoodsDTO>): RecyclerView.Adapter<GoodsAdapter.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener

    var selPos = 0

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])

        if(selPos == position)
            holder.binding.menu.setBackgroundResource(R.drawable.gradient_main)
        else
            holder.binding.menu.setBackgroundColor(Color.WHITE)

        holder.binding.menu.setOnClickListener{
            val beforePos = selPos
            selPos = position

            if(selPos != beforePos) {
                notifyItemChanged(selPos)
                notifyItemChanged(beforePos)
                itemClickListener.onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding:ListMenuBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: GoodsDTO) {
            binding.run {
                name.text = data.name
                price.text = AppHelper.price(data.price)

                var status = 0
                when(data.icon) {
                    3 -> status = R.drawable.img_best
                    5 -> status = R.drawable.img_new
                    4 -> status = R.drawable.img_soldout
                    else -> binding.icon.visibility = View.GONE
                }
                if(status != 0) {
                    icon.setImageResource(status)
                    icon.visibility = View.VISIBLE
                }
            }
        }
    }
}