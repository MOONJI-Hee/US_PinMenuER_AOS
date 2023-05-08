package com.wooriyo.pinmenuer.menu.adpter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ListOptBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.OptionDTO

class OptAdapter(val dataSet: ArrayList<OptionDTO>): RecyclerView.Adapter<OptAdapter.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListOptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.rvVals.layoutManager = LinearLayoutManager(parent.context, RecyclerView.VERTICAL, false)
        return ViewHolder(binding, itemClickListener, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    class ViewHolder(val binding:ListOptBinding, val itemClickListener: ItemClickListener, val context: Context): RecyclerView.ViewHolder(binding.root) {

        fun bind(data: OptionDTO) {
            binding.name.text = data.name

            when(data.optreq) {
                0 -> binding.type.text = context.getString(R.string.optional)
                1 -> binding.type.text = context.getString(R.string.essential)
            }
            binding.rvVals.adapter = ValueAdapter(data)

            binding.layout.setOnClickListener { itemClickListener.onItemClick(absoluteAdapterPosition) }
        }
    }

}