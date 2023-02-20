package com.wooriyo.pinmenuer.call.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.call.adapter.CallListAdapter.ViewHolder
import com.wooriyo.pinmenuer.databinding.ListCallBinding
import com.wooriyo.pinmenuer.model.CallListDTO

class CallListAdapter(val dataSet: ArrayList<CallListDTO>): RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListCallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.rv.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.VERTICAL, false)
        return ViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val context: Context, val binding: ListCallBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind (data : CallListDTO) {
//            binding.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rv.adapter = CallDetailAdapter(data.callList)

            binding.tableNo.text = data.tableNo

            val date = data.regdt.split(" ")[0].replace("-", ".")
            val time = data.regdt.split(" ")[1].substring(0, 5)

            binding.date.text = date
            binding.time.text = time
        }

    }
}