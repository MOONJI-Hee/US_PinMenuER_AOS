package com.wooriyo.pinmenuer.history.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.databinding.ListTableBinding
import com.wooriyo.pinmenuer.history.TableHisActivity
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.OrderHistoryDTO
import com.wooriyo.pinmenuer.util.AppHelper

class TableAdapter(val dataSet: ArrayList<OrderHistoryDTO>): RecyclerView.Adapter<TableAdapter.ViewHolder>() {
    lateinit var completeClickListener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListTableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, completeClickListener, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setOnCompleteClickListener(completeClickListener: ItemClickListener) {
        this.completeClickListener = completeClickListener
    }

    class ViewHolder(val binding: ListTableBinding, val completeClickListener: ItemClickListener, val context: Context): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: OrderHistoryDTO) {
            binding.run {
                tableNo.text = data.tableNo

                ordCnt.text = data.totalOrdCnt.toString()
                callCnt.text = data.totalCallCnt.toString()

                totCnt.text = data.total.toString()
                totPrice.text = AppHelper.price(data.total_price)

                if(data.total == data.completedGea && data.total_price == data.completedPrice) {
                    completeCnt.visibility = View.GONE
                    tvGea.visibility = View.GONE
                }else {
                    completeCnt.visibility = View.VISIBLE
                    tvGea.visibility = View.VISIBLE
                    completeCnt.text = data.completedGea.toString()
                }

                completePrice.text = AppHelper.price(data.completedPrice)

                val remain = data.total_price - data.completedPrice
                remainPrice.text = AppHelper.price(remain)

                btnComplete.setOnClickListener {
                    completeClickListener.onItemClick(absoluteAdapterPosition)
                }

                layout.setOnClickListener {
                    val intent = Intent(context, TableHisActivity::class.java)
                    intent.putExtra("tableNo", data.tableNo)
                    context.startActivity(intent)
                }
            }
        }
    }
}