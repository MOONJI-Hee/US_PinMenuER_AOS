package com.wooriyo.us.pinmenuer.menu.adpter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.us.pinmenuer.databinding.ListCateBinding
import com.wooriyo.us.pinmenuer.listener.ItemClickListener
import com.wooriyo.us.pinmenuer.model.CategoryDTO

class CateAdapter(val dataSet: ArrayList<CategoryDTO>, val type: Int) : RecyclerView.Adapter<CateAdapter.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener

    var selPos = 0

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListCateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemCount-1)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])

        if(type == 1) {
            if(selPos == position) {
                holder.binding.selected.visibility = View.VISIBLE
                holder.binding.subName.isSelected = true
//                binding.subName.setTextColor(Color.WHITE)
            } else {
                holder.binding.selected.visibility = View.GONE
                holder.binding.subName.isSelected = false
//                binding.subName.setTextColor(Color.parseColor("#C4C4C4"))
            }

            holder.binding.layout.setOnClickListener{
                val beforePos = selPos
                selPos = position

                if(selPos != beforePos) {
                    notifyItemChanged(selPos)
                    notifyItemChanged(beforePos)
                    itemClickListener.onItemClick(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListCateBinding, val lastPos : Int) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CategoryDTO) {
            binding.name.text = data.name
            binding.subName.text = data.subname

            if (data.buse == "N") {
                binding.name.setTextColor(Color.parseColor("#696969"))
//                binding.subName.setTextColor(Color.parseColor("#696969"))
                binding.subName.isEnabled = false
            } else {
                binding.name.setTextColor(Color.WHITE)
//                binding.subName.setTextColor(Color.parseColor("#C4C4C4"))
                binding.subName.isEnabled = true
            }

            if(absoluteAdapterPosition == lastPos)
                binding.backline.visibility = View.VISIBLE
            else
                binding.backline.visibility = View.GONE
        }
    }
}