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
import java.util.*
import kotlin.collections.ArrayList

class GoodsAdapter(val dataSet: ArrayList<GoodsDTO>): Adapter<RecyclerView.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener
    lateinit var deleteListener: ItemClickListener
    lateinit var moveListener: ItemClickListener

    var selPos = 0
    var mode = 0

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun setOnDeleteListener(deleteListener: ItemClickListener) {
        this.deleteListener = deleteListener
    }

    fun setOnMoveListener(moveListener: ItemClickListener) {
        this.moveListener = moveListener
    }

    fun setMenuMode(mode: Int) {
        this.mode = mode
        notifyDataSetChanged()
    }

    fun swapData(fromPos: Int, toPos: Int) {
        Collections.swap(dataSet, fromPos, toPos)
        notifyItemMoved(fromPos, toPos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(dataSet[position], mode, dataSet.size-1)

        if(selPos == position) {
            holder.binding.layout.setBackgroundResource(R.drawable.gradient_main)
            holder.binding.ivPlus.setImageResource(R.drawable.img_menu_plus_s)
        } else {
            holder.binding.layout.setBackgroundColor(Color.WHITE)
            holder.binding.ivPlus.setImageResource(R.drawable.img_menu_plus)
        }

        holder.binding.layout.setOnClickListener {
            val beforePos = selPos
            selPos = position

            if(selPos != beforePos) {
                notifyItemChanged(selPos)
                notifyItemChanged(beforePos)

                if(mode == 4) { // 삭제모드일 때
                    deleteListener.onItemClick(position)
                }else if(mode == 3)  // 추가, 수정모드일 때
                    moveListener.onItemClick(position)
                else
                    itemClickListener.onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return if(mode == 4 || mode == 3) dataSet.size-1 else dataSet.size
    }

    class ViewHolder(val binding:ListMenuBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: GoodsDTO, mode: Int, lastIndex: Int) {
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
                        else -> binding.icon.visibility = View.GONE
                    }

                    if(data.icon == 4) {
                        soldout.visibility = View.VISIBLE
                    }else {
                        soldout.visibility = View.GONE
                        if(status != 0) {
                            icon.setImageResource(status)
                            icon.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }
}