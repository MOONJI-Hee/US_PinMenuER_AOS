package com.wooriyo.us.pinmenuer.history.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.us.pinmenuer.databinding.ListTableBinding
import com.wooriyo.us.pinmenuer.history.TableHisActivity
import com.wooriyo.us.pinmenuer.listener.ItemClickListener
import com.wooriyo.us.pinmenuer.model.OrderHistoryDTO
import com.wooriyo.us.pinmenuer.util.AppHelper

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
                // TODO API 확인 및 기능 구현
                if(data.tableNo == "Res.") {
                    clOrder.visibility = View.INVISIBLE
                    clCall.visibility = View.INVISIBLE
                    clReservStore.visibility = View.VISIBLE
                    clReservTogo.visibility = View.VISIBLE

                    reservCnt.text = data.reservStoreCnt.toString()
                    togoCnt.text = data.reservTogoCnt.toString()

                    tableNo.setTextColor(Color.parseColor("#EC345B"))
                }else {
                    clOrder.visibility = View.VISIBLE
                    clCall.visibility = View.VISIBLE
                    clReservStore.visibility = View.GONE
                    clReservTogo.visibility = View.GONE

                    ordCnt.text = data.totalOrdCnt.toString()
                    callCnt.text = data.totalCallCnt.toString()

                    tableNo.setTextColor(Color.parseColor("#000000"))
                }

                tableNo.text = data.tableNo

                totCnt.text = data.total.toString()
                totPrice.text = AppHelper.price(data.total_price)

                if(data.total == data.completedGea && data.total_price == data.completedPrice) {
                    completeCnt.visibility = View.GONE
                }else {
                    completeCnt.visibility = View.VISIBLE
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