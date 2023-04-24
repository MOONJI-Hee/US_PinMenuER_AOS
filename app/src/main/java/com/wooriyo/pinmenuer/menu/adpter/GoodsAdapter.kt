package com.wooriyo.pinmenuer.menu.adpter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ListMenuBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.GoodsDTO
import com.wooriyo.pinmenuer.util.AppHelper

class GoodsAdapter(val dataSet: ArrayList<GoodsDTO>): Adapter<RecyclerView.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener

    var selPos = 0
    var mode = 0

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun setMenuMode(mode: Int) {
        this.mode = mode
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(dataSet[position], dataSet.size-1)

        if(selPos == position) {
            holder.binding.layout.setBackgroundResource(R.drawable.gradient_main)
            holder.binding.ivPlus.setImageResource(R.drawable.ic_plus_b)
        } else {
            holder.binding.layout.setBackgroundColor(Color.WHITE)
            holder.binding.ivPlus.setImageResource(R.drawable.ic_plus_g)
        }

        holder.binding.layout.setOnClickListener {
            val beforePos = selPos
            selPos = position

            if(selPos != beforePos) {
                notifyItemChanged(selPos)
                notifyItemChanged(beforePos)

                if(mode == 3) {
                    // 드래그 할 수 있도록 설정
                } else
                    itemClickListener.onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding:ListMenuBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: GoodsDTO, lastIndex: Int) {
            binding.run {
                if(absoluteAdapterPosition == lastIndex) {
                    menu.visibility = View.GONE
                    ivPlus.visibility = View.VISIBLE
                } else {
                    menu.visibility = View.VISIBLE
                    ivPlus.visibility = View.GONE

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
}