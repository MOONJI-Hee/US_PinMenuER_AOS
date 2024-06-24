package com.wooriyo.us.pinmenuer.printer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.databinding.ListSelCateBinding
import com.wooriyo.us.pinmenuer.listener.ItemClickListener
import com.wooriyo.us.pinmenuer.model.CategoryDTO

class SelCateAdapter(val dataSet: ArrayList<CategoryDTO>): RecyclerView.Adapter<SelCateAdapter.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener

    fun setOnCheckClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListSelCateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListSelCateBinding, val itemClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CategoryDTO) {
            binding.name.text = data.name

            if(data.checked == 1)
                binding.name.isChecked = true

            binding.name.setOnCheckedChangeListener { v, isChecked ->
                itemClickListener.onCheckClick(absoluteAdapterPosition, v as CheckBox, isChecked)
            }
        }
    }
}