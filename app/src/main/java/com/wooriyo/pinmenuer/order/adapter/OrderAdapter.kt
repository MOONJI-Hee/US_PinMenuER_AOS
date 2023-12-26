package com.wooriyo.pinmenuer.order.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.common.InfoDialog
import com.wooriyo.pinmenuer.databinding.ListOrderBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.OrderHistoryDTO
import com.wooriyo.pinmenuer.util.AppHelper
import java.io.IOException

class OrderAdapter(val dataSet: ArrayList<OrderHistoryDTO>): RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    lateinit var completeListener: ItemClickListener
    lateinit var deleteListener: ItemClickListener
    lateinit var printClickListener: ItemClickListener

    fun setOnCompleteListener(completeListener: ItemClickListener) {
        this.completeListener = completeListener
    }

    fun setOnDeleteListener(deleteListener: ItemClickListener) {
        this.deleteListener = deleteListener
    }

    fun setOnPrintClickListener(printClickListener: ItemClickListener) {
        this.printClickListener = printClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.rv.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.VERTICAL, false)
        return ViewHolder(parent.context, binding, completeListener, deleteListener, printClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(val context: Context, val binding: ListOrderBinding, val completeListener: ItemClickListener, val deleteListener: ItemClickListener, val printClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind (data : OrderHistoryDTO) {
            binding.run {
                rv.adapter = OrderDetailAdapter(data.olist)

                tableNo.text = data.tableNo
                regdt.text = data.regdt
                gea.text = data.total.toString()
                price.text = AppHelper.price(data.amount)

                if(data.iscompleted == 1) {
                    tableNo.setBackgroundColor(Color.parseColor("#E0E0E0"))
                    clPrice.setBackgroundResource(R.drawable.bg_cancel_r6)
                    btnComplete.setBackgroundResource(R.drawable.bg_cancel_r6)
                    btnComplete.text = "복원"
                    complete.visibility = View.VISIBLE
                    completeQr.visibility = View.GONE
                    completePos.visibility = View.GONE
                } else if (data.paytype == 3) { // QR오더에서 들어온 주문 > 결제 완료
                    tableNo.setBackgroundResource(R.color.main)
                    clPrice.setBackgroundResource(R.drawable.bg_cancel_r6)
                    btnComplete.setBackgroundResource(R.drawable.bg_r6y)
                    btnComplete.text = "완료"
                    complete.visibility = View.GONE
                    completeQr.visibility = View.VISIBLE
                    completePos.visibility = View.GONE
                } else if (data.paytype == 4) {
                    tableNo.setBackgroundResource(R.color.main)
                    clPrice.setBackgroundResource(R.drawable.bg_r6y)
                    btnComplete.setBackgroundResource(R.drawable.bg_r6y)
                    btnComplete.text = "완료"
                    complete.visibility = View.GONE
                    completeQr.visibility = View.GONE
                    completePos.visibility = View.VISIBLE
                } else {
                    tableNo.setBackgroundResource(R.color.main)
                    clPrice.setBackgroundResource(R.drawable.bg_r6y)
                    btnComplete.setBackgroundResource(R.drawable.bg_r6y)
                    btnComplete.text = "완료"
                    complete.visibility = View.GONE
                    completeQr.visibility = View.GONE
                    completePos.visibility = View.GONE
                }

                delete.setOnClickListener { deleteListener.onItemClick(absoluteAdapterPosition) }
                print.setOnClickListener {
                    if(MyApplication.bluetoothPort.isConnected) {
                        printClickListener.onItemClick(absoluteAdapterPosition)
                    }else
                        InfoDialog(context, "", context.getString(R.string.dialog_no_printer)).show()
                }
                btnComplete.setOnClickListener { completeListener.onItemClick(absoluteAdapterPosition) }
            }
        }
    }
}