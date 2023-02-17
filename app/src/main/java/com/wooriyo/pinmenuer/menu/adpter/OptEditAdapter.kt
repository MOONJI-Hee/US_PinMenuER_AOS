package com.wooriyo.pinmenuer.menu.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.databinding.ListOptEditBinding
import com.wooriyo.pinmenuer.model.OptionDTO
import com.wooriyo.pinmenuer.util.AppHelper

class OptEditAdapter(val opt: OptionDTO): RecyclerView.Adapter<OptEditAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListOptEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(itemCount - 1 == position) {
            holder.binding.btnPlus.visibility = View.VISIBLE
            holder.binding.llEdit.visibility = View.GONE
        } else {
            holder.binding.btnPlus.visibility = View.GONE
            holder.binding.llEdit.visibility = View.VISIBLE
        }
        holder.bind(opt, position)
    }

    override fun getItemCount(): Int {
        return opt.optval.size
    }

    class ViewHolder(val binding: ListOptEditBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(opt: OptionDTO, position: Int) {
            binding.run {
                etVal.setText(opt.optval[position])

                val pay = opt.optpay
                if(pay != null) {
                    etPrice.setText(AppHelper.price(pay[position]))
                }
                // TODO + / - 스피너 초기화
            }
        }
    }
}