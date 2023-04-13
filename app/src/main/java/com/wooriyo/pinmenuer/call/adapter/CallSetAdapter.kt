package com.wooriyo.pinmenuer.call.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.call.dialog.CallDialog
import com.wooriyo.pinmenuer.config.AppProperties
import com.wooriyo.pinmenuer.databinding.ListCallAddBinding
import com.wooriyo.pinmenuer.databinding.ListCallSetBinding
import com.wooriyo.pinmenuer.databinding.ListStoreAddBinding
import com.wooriyo.pinmenuer.listener.DialogListener
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.CallDTO
import com.wooriyo.pinmenuer.store.StoreSetActivity
import com.wooriyo.pinmenuer.store.adpter.StoreAdapter

class CallSetAdapter(val dataSet: ArrayList<CallDTO>): RecyclerView.Adapter<RecyclerView.ViewHolder>(), DialogListener {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListCallSetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val binding_add = ListCallAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return if(viewType == AppProperties.VIEW_TYPE_ADD) AddViewHolder(binding_add, parent.context) else ViewHolder(binding, parent.context)
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

    class ViewHolder(val binding:ListCallSetBinding, val context: Context): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CallDTO) {
            binding.name.text = data.name
            binding.name.setOnClickListener {
                CallDialog(context, absoluteAdapterPosition, data).show()
            }
        }
    }

    class AddViewHolder(val binding: ListCallAddBinding, val context: Context): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.plus.setOnClickListener{
                CallDialog(context, -1, CallDTO()).show()
            }
        }
    }
}