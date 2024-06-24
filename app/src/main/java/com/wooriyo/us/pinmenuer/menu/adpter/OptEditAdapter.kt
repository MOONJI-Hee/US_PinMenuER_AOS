package com.wooriyo.us.pinmenuer.menu.adpter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.R
import com.wooriyo.us.pinmenuer.config.AppProperties
import com.wooriyo.pinmenuer.databinding.ListOptAddBinding
import com.wooriyo.pinmenuer.databinding.ListOptEditBinding
import com.wooriyo.us.pinmenuer.model.ValueDTO
import com.wooriyo.us.pinmenuer.util.AppHelper

class OptEditAdapter(val dataSet: ArrayList<ValueDTO>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListOptEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val bindingAdd = ListOptAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val arrayAdapter = ArrayAdapter.createFromResource(parent.context, R.array.opt_mark, R.layout.spinner_opt_mark)
        binding.spMark.adapter = arrayAdapter

        return if(viewType == AppProperties.VIEW_TYPE_ADD) AddViewHolder(bindingAdd)
        else ViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            AppProperties.VIEW_TYPE_ADD -> {
                holder as AddViewHolder
                holder.bind()

                holder.binding.btnPlus.setOnClickListener {
                    dataSet.add(ValueDTO())
                    notifyItemInserted(position)
                    notifyItemChanged(position+1)
                }
            }
            else -> {
                holder as ViewHolder
                holder.bind(dataSet[position])

                holder.binding.tvDel.setOnClickListener {
                    dataSet.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount - position) // 삭제한 아이템 ~ 뒤의 모든 아이템 수정 (포지션 재배치 및 옵션 번호 다시 출력)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == dataSet.size) AppProperties.VIEW_TYPE_ADD else position
    }

    class ViewHolder(val binding: ListOptEditBinding, val context: Context): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ValueDTO) {
            binding.run {
                num.text = "옵션${absoluteAdapterPosition + 1}"

                etVal.setText(data.name)

                if(data.price == "0" || data.price.isEmpty())
                    etPrice.setText("")
                 else
                    etPrice.setText(AppHelper.price(data.price.toInt()))

                if (data.mark == "-")
                    spMark.setSelection(1)
                else
                    spMark.setSelection(0)

                etVal.addTextChangedListener {
                    data.name = it.toString()
                }

                etPrice.addTextChangedListener(object : TextWatcher {
                    var result = ""
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if(!s.isNullOrEmpty() && s.toString() != result) {
                            result = AppHelper.price(s.toString().replace(",", "").toInt())
                            binding.etPrice.setText(result)
                            binding.etPrice.setSelection(result.length)
                        }
                    }
                    override fun afterTextChanged(s: Editable?) {
                        if(s != null) {
                            data.price = (s.toString()).replace(",", "")
                        }
                    }
                })

                spMark.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        data.mark = p0?.selectedItem.toString()
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }
            }
        }
    }

    class AddViewHolder(val binding: ListOptAddBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.num.text = "옵션${absoluteAdapterPosition + 1}"
        }
    }
}