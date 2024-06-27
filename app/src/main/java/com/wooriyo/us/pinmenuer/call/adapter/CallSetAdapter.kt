package com.wooriyo.us.pinmenuer.call.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.us.pinmenuer.config.AppProperties
import com.wooriyo.us.pinmenuer.databinding.ListCallAddBinding
import com.wooriyo.us.pinmenuer.databinding.ListCallSetBinding
import com.wooriyo.us.pinmenuer.listener.ItemClickListener
import com.wooriyo.us.pinmenuer.model.CallDTO

class CallSetAdapter(val dataSet: ArrayList<CallDTO>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListCallSetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val binding_add = ListCallAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return if(viewType == AppProperties.VIEW_TYPE_ADD) AddViewHolder(binding_add, parent.context, itemClickListener) else ViewHolder(binding, parent.context, itemClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == AppProperties.VIEW_TYPE_COM)
            (holder as ViewHolder).bind(dataSet[position])
        else
            (holder as AddViewHolder).bind()
    }

    override fun getItemCount(): Int {
        return dataSet.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == itemCount -1) AppProperties.VIEW_TYPE_ADD else AppProperties.VIEW_TYPE_COM
    }

    class ViewHolder(val binding:ListCallSetBinding, val context: Context, val itemClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CallDTO) {
            binding.name.text = data.name
            binding.name.setOnClickListener {
                itemClickListener.onCallClick(absoluteAdapterPosition, data)
            }
        }
    }

    class AddViewHolder(val binding: ListCallAddBinding, val context: Context, val itemClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.plus.setOnClickListener{
                itemClickListener.onCallClick(-1, CallDTO())
            }
        }
    }
}