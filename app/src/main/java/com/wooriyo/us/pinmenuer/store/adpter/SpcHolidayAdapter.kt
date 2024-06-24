package com.wooriyo.us.pinmenuer.store.adpter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.databinding.ListSpecialHolidayBinding
import com.wooriyo.us.pinmenuer.listener.ItemClickListener
import com.wooriyo.us.pinmenuer.model.SpcHolidayDTO

class SpcHolidayAdapter(val dataSet: ArrayList<SpcHolidayDTO>) : RecyclerView.Adapter<SpcHolidayAdapter.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener

    fun setOnItemClick (itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListSpecialHolidayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
        holder.binding.layout.setOnClickListener {
            itemClickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListSpecialHolidayBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SpcHolidayDTO) {
            binding.holidayName.text = data.title
            if(data.month == "") {
                binding.holidayDate.text = "매월 ${data.day}일"
            }else {
                binding.holidayDate.text = "${data.month}월 ${data.day}일"
            }
        }
    }
}