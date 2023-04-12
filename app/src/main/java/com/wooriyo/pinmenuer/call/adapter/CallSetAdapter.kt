package com.wooriyo.pinmenuer.call.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.call.dialog.CallDialog
import com.wooriyo.pinmenuer.databinding.ListCallSetBinding
import com.wooriyo.pinmenuer.model.CallDTO

class CallSetAdapter(val dataSet: ArrayList<CallDTO>): RecyclerView.Adapter<CallSetAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListCallSetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position == dataSet.lastIndex) {
            holder.binding.plus.visibility = View.VISIBLE
            holder.binding.name.visibility = View.GONE
        } else {
            holder.binding.plus.visibility = View.GONE
            holder.binding.name.visibility = View.VISIBLE
        }
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding:ListCallSetBinding, val context: Context): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CallDTO) {
            binding.name.text = data.name

            binding.name.setOnClickListener {
                CallDialog(context, absoluteAdapterPosition, data).show()
            }
            binding.plus.setOnClickListener{
                CallDialog(context,-1, data).show()
            }
        }
    }
}