package com.wooriyo.pinmenuer.menu.adpter

import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.call.adapter.CallSetAdapter
import com.wooriyo.pinmenuer.config.AppProperties
import com.wooriyo.pinmenuer.databinding.ListCallAddBinding
import com.wooriyo.pinmenuer.databinding.ListOptAddBinding
import com.wooriyo.pinmenuer.databinding.ListOptEditBinding
import com.wooriyo.pinmenuer.model.OptionDTO
import com.wooriyo.pinmenuer.util.AppHelper

class OptEditAdapter(val opt: OptionDTO): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListOptEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val binding_add = ListOptAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val marks = parent.context.resources.getStringArray(R.array.opt_mark)
        val arrayAdapter = ArrayAdapter<String>(parent.context, android.R.layout.simple_spinner_item, marks)

        return if(viewType == AppProperties.VIEW_TYPE_ADD) AddViewHolder(binding_add) else ViewHolder(binding,arrayAdapter)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(position == itemCount -1) {
            (holder as AddViewHolder).bind()
            holder.binding.btnPlus.setOnClickListener {
                opt.optval.add("")
                opt.optpay.add("")
                opt.optmark.add("")
                notifyItemInserted(position)
                notifyItemChanged(position+1)
            }
        } else {
            (holder as ViewHolder).bind(opt)
            holder.binding.tvDel.setOnClickListener {
                opt.optval.removeAt(position)
                opt.optpay.removeAt(position)
                opt.optmark.removeAt(position)
                notifyItemRemoved(position)     // 아이템 삭제
                notifyItemRangeChanged(position, opt.optval.size - position)    // 삭제한 아이템 뒤의 모든 아이템 수정 (포지션 재배치 및 옵션 번호 다시 출력)
                notifyItemChanged(opt.optval.size)  // 플러스 버튼
            }
        }
    }

    override fun getItemCount(): Int {
        return opt.optval.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == itemCount -1) AppProperties.VIEW_TYPE_ADD else AppProperties.VIEW_TYPE_COM
    }

    class ViewHolder(val binding: ListOptEditBinding, val arrayAdapter: ArrayAdapter<String>): RecyclerView.ViewHolder(binding.root) {
        fun bind(opt: OptionDTO) {
            binding.run {
                num.text = "옵션${absoluteAdapterPosition + 1}"
                etVal.setText(opt.optval[absoluteAdapterPosition])
                etVal.addTextChangedListener {
                    opt.optval[absoluteAdapterPosition] = it.toString()
                }

                val pay = opt.optpay[absoluteAdapterPosition]
                if(pay.isNotEmpty() || pay != "0") {
                    etPrice.setText(pay)
                }
                etPrice.addTextChangedListener {
                    opt.optpay[absoluteAdapterPosition] = it.toString()
                }

                spMark.adapter = arrayAdapter
                spMark.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        opt.optmark[absoluteAdapterPosition] = p0?.selectedItem.toString()
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }
                if (opt.optmark[absoluteAdapterPosition] == "-")
                    spMark.setSelection(1)
                else
                    spMark.setSelection(0)
            }
        }
    }

    class AddViewHolder(val binding: ListOptAddBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.num.text = "옵션${absoluteAdapterPosition + 1}"
        }
    }
}