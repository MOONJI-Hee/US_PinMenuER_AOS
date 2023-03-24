package com.wooriyo.pinmenuer.call.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.call.adapter.CallListAdapter.ViewHolder
import com.wooriyo.pinmenuer.databinding.ListCallBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.CallHistoryDTO
import com.wooriyo.pinmenuer.model.CallListDTO

class CallListAdapter(val dataSet: ArrayList<CallHistoryDTO>): RecyclerView.Adapter<ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListCallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.rv.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.VERTICAL, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position], itemClickListener)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListCallBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind (data : CallHistoryDTO, itemClickListener : ItemClickListener) {
//            binding.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rv.adapter = CallDetailAdapter(data.clist)

            binding.tableNo.text = data.tableNo

            val date = data.regdt.split(" ")[0].replace("-", ".")
            val time = data.regdt.split(" ")[1].substring(0, 5)

            binding.date.text = date
            binding.time.text = time

            binding.complete.setOnClickListener { itemClickListener.onItemClick(absoluteAdapterPosition) }
        }

    }
}