package com.wooriyo.pinmenuer.menu.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ListOptAddBinding
import com.wooriyo.pinmenuer.databinding.ListOptEditBinding
import com.wooriyo.pinmenuer.model.OptionDTO
import com.wooriyo.pinmenuer.util.AppHelper

class OptEditAdapter(val opt: OptionDTO): RecyclerView.Adapter<OptEditAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListOptEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val marks = parent.context.resources.getStringArray(R.array.opt_mark)
        val arrayAdapter = ArrayAdapter<String>(parent.context, android.R.layout.simple_spinner_item, marks)

        return ViewHolder(binding,arrayAdapter)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(opt)

        if(itemCount - 1 == position) {
            holder.binding.btnPlus.visibility = View.VISIBLE
            holder.binding.llEdit.visibility = View.GONE
        } else {
            holder.binding.btnPlus.visibility = View.GONE
            holder.binding.llEdit.visibility = View.VISIBLE
        }

        holder.binding.btnPlus.setOnClickListener {
            opt.optval.add("")
            opt.optpay.add("")
            opt.optmark.add("")
            notifyItemInserted(position)
        }
    }

    override fun getItemCount(): Int {
        return opt.optval.size
    }

    class ViewHolder(val binding: ListOptEditBinding, val arrayAdapter: ArrayAdapter<String>): RecyclerView.ViewHolder(binding.root) {
        fun bind(opt: OptionDTO) {
            binding.run {
                etVal.setText(opt.optval[adapterPosition])

                val pay = opt.optpay[adapterPosition]
                if(pay.isEmpty() || pay == "0") {
                    etPrice.setText(pay)
                }

                spMark.adapter = arrayAdapter

                if (opt.optmark[adapterPosition] == "+")
                    spMark.setSelection(0)
                else
                    spMark.setSelection(1)

                spMark.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        opt.optmark[adapterPosition] = p0?.selectedItem.toString()
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {
//                        p0?.setSelection(0)
                    }
                }
            }
        }
    }

    class AddViewHolder(val binding: ListOptAddBinding) {
        fun bind(opt: OptionDTO) {
        }
    }
}