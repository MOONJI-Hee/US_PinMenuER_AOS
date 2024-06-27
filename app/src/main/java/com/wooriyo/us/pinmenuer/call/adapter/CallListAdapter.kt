package com.wooriyo.us.pinmenuer.call.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.call.adapter.CallListAdapter.ViewHolder
import com.wooriyo.us.pinmenuer.databinding.ListCallBinding
import com.wooriyo.us.pinmenuer.listener.ItemClickListener
import com.wooriyo.us.pinmenuer.model.CallHistoryDTO

class CallListAdapter(val dataSet: ArrayList<CallHistoryDTO>): RecyclerView.Adapter<ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener
    lateinit var deleteListener: ItemClickListener

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun setOnDeleteListener(deleteListener: ItemClickListener) {
        this.deleteListener = deleteListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListCallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.rv.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.VERTICAL, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position], itemClickListener, deleteListener)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListCallBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind (data : CallHistoryDTO, itemClickListener : ItemClickListener, deleteListener: ItemClickListener) {
            binding.run {
                rv.adapter = CallDetailAdapter(data.clist)

                tableNo.text = data.tableNo
                regdt.text = data.regDt

                if(data.iscompleted == 1) {
                    top.setBackgroundColor(Color.parseColor("#E0E0E0"))
                    done.visibility = View.VISIBLE
                    complete.isEnabled = false
                }else {
                    top.setBackgroundResource(R.color.main)
                    done.visibility = View.GONE
                    complete.isEnabled = true
                }

                delete.setOnClickListener { deleteListener.onItemClick(absoluteAdapterPosition) }
                complete.setOnClickListener { itemClickListener.onItemClick(absoluteAdapterPosition) }
            }
        }

    }
}