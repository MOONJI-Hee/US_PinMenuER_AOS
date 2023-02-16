package com.wooriyo.pinmenuer.menu.adpter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.databinding.ListCateBinding
import com.wooriyo.pinmenuer.model.CategoryDTO

class CateAdapter(val dataSet: ArrayList<CategoryDTO>) : RecyclerView.Adapter<CateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListCateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])

        if(position == dataSet.lastIndex)
            holder.binding.backline.visibility = View.VISIBLE
        else
            holder.binding.backline.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListCateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CategoryDTO) {
            binding.name.text = data.name
            binding.subName.text = data.subname

            if (data.buse == "N") {
                binding.name.setTextColor(Color.parseColor("#696969"))
                binding.subName.setTextColor(Color.parseColor("#696969"))
            } else {
                binding.name.setTextColor(Color.WHITE)
                binding.subName.setTextColor(Color.WHITE)
            }
        }
    }
}