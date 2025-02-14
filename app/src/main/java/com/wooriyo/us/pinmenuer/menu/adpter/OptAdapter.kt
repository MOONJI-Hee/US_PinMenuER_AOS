package com.wooriyo.us.pinmenuer.menu.adpter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.databinding.ListOptBinding
import com.wooriyo.us.pinmenuer.listener.ItemClickListener
import com.wooriyo.us.pinmenuer.model.OptionDTO
import com.wooriyo.us.pinmenuer.model.ValueDTO

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
            binding.name.text = data.title

            when(data.optreq) {
                0 -> {
                    binding.type.text = context.getString(R.string.optional)
                    binding.type.setTextColor(Color.parseColor("#FFA701"))
                }
                1 -> {
                    binding.type.text = context.getString(R.string.essential)
                    binding.type.setTextColor(Color.parseColor("#FF0000"))
                }
            }
            binding.rvVals.adapter = ValueAdapter(data.optval ?: ArrayList<ValueDTO>())

            binding.layout.setOnClickListener { itemClickListener.onItemClick(absoluteAdapterPosition) }
        }
    }

}