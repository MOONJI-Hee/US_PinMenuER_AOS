package com.wooriyo.pinmenuer.menu.adpter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ListCateEditBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.CategoryDTO

class CateEditAdapter(val dataSet: ArrayList<CategoryDTO>): RecyclerView.Adapter<CateEditAdapter.ViewHolder>() {
    lateinit var itemMoveListener: ItemClickListener

    fun setOnMoveListener(itemMoveListener: ItemClickListener) {
        this.itemMoveListener = itemMoveListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListCateEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])

        holder.binding.left.setOnClickListener {
            Log.d("CateEditAdapter", "BindViewHolder 안의 adpterposition $position")
            if(position != 0) {
                itemMoveListener.onItemMove(position, position-1)
            }
        }

        holder.binding.right.setOnClickListener {
            Log.d("CateEditAdapter", "BindViewHolder 안의 adpterposition $position")
            if(position != dataSet.lastIndex) {
                itemMoveListener.onItemMove(position, position+1)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val binding: ListCateEditBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CategoryDTO) {
            binding.run {
                name.text = data.name
                subName.text = data.subname

                if (data.buse == "N") {
                    name.setTextColor(Color.parseColor("#696969"))
                    subName.setTextColor(Color.parseColor("#696969"))
                } else {
                    name.setTextColor(Color.BLACK)
                    subName.setTextColor(Color.BLACK)
                }

                seq.setOnClickListener {
                    it.setBackgroundResource(R.drawable.bg_btn_r6_grd)
                    left.visibility = View.VISIBLE
                    right.visibility = View.VISIBLE
                }

                Log.d("CateEditAdapter", "ViewHolder 안의 adpterposition $adapterPosition")
            }
        }
    }
}