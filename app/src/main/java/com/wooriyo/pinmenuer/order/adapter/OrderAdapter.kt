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
import com.wooriyo.pinmenuer.config.AppProperties.Companion.VIEW_TYPE_ORDER
import com.wooriyo.pinmenuer.config.AppProperties.Companion.VIEW_TYPE_RESERVATION
import com.wooriyo.pinmenuer.databinding.ListOrderBinding
import com.wooriyo.pinmenuer.databinding.ListReservationBinding
import com.wooriyo.pinmenuer.history.adapter.ReservationAdapter
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.OrderHistoryDTO
import com.wooriyo.pinmenuer.util.AppHelper
import java.io.IOException

class OrderAdapter(val dataSet: ArrayList<OrderHistoryDTO>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var completeListener: ItemClickListener
    lateinit var confirmListener: ItemClickListener
    lateinit var deleteListener: ItemClickListener
    lateinit var printClickListener: ItemClickListener
    lateinit var setTableNoListener: ItemClickListener

    fun setOnCompleteListener(completeListener: ItemClickListener) {
        this.completeListener = completeListener
    }

    fun setOnConfirmListener(confirmListener: ItemClickListener) {
        this.confirmListener = confirmListener
    }

    fun setOnDeleteListener(deleteListener: ItemClickListener) {
        this.deleteListener = deleteListener
    }

    fun setOnPrintClickListener(printClickListener: ItemClickListener) {
        this.printClickListener = printClickListener
    }

    fun setOnTableNoListener(setTableNoListener: ItemClickListener) {
        this.setTableNoListener = setTableNoListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.rv.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.VERTICAL, false)

        val reservBinding = ListReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        reservBinding.rv.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.VERTICAL, false)

        return if(viewType == VIEW_TYPE_ORDER) ViewHolder(binding, parent.context, completeListener, deleteListener, printClickListener)
        else ReservationAdapter.ViewHolder(reservBinding, parent.context, completeListener, confirmListener, deleteListener, printClickListener, setTableNoListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == VIEW_TYPE_ORDER) {
            holder as ViewHolder
            holder.bind(dataSet[position])
        }else {
            holder as ReservationAdapter.ViewHolder
            holder.bind(dataSet[position])
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(dataSet[position].reserType == 0) VIEW_TYPE_ORDER else VIEW_TYPE_RESERVATION
    }

    class ViewHolder(val binding: ListOrderBinding, val context: Context, val completeListener: ItemClickListener, val deleteListener: ItemClickListener, val printClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind (data : OrderHistoryDTO) {
            binding.run {
                rv.adapter = OrderDetailAdapter(data.olist)

                tableNo.text = data.tableNo
                regdt.text = data.regdt
                orderNo.text = data.ordcode
                gea.text = data.total.toString()
                price.text = AppHelper.price(data.amount)

                if(data.iscompleted == 1) {
                    top.setBackgroundColor(Color.parseColor("#E0E0E0"))
                    clPrice.setBackgroundResource(R.drawable.bg_cancel_r6)
                    btnComplete.setBackgroundResource(R.drawable.bg_cancel_r6)
                    btnComplete.text = "복원"
                    complete.visibility = View.VISIBLE
                    completeQr.visibility = View.GONE
                    completePos.visibility = View.GONE
                } else if (data.paytype == 3) { // QR오더에서 들어온 주문 > 결제 완료
                    top.setBackgroundResource(R.color.main)
                    clPrice.setBackgroundResource(R.drawable.bg_cancel_r6)
                    btnComplete.setBackgroundResource(R.drawable.bg_r6y)
                    btnComplete.text = "완료"
                    complete.visibility = View.GONE
                    completeQr.visibility = View.VISIBLE
                    completePos.visibility = View.GONE
                } else if (data.paytype == 4) {
                    top.setBackgroundResource(R.color.main)
                    clPrice.setBackgroundResource(R.drawable.bg_r6y)
                    btnComplete.setBackgroundResource(R.drawable.bg_r6y)
                    btnComplete.text = "완료"
                    complete.visibility = View.GONE
                    completeQr.visibility = View.GONE
                    completePos.visibility = View.VISIBLE
                } else {
                    top.setBackgroundResource(R.color.main)
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