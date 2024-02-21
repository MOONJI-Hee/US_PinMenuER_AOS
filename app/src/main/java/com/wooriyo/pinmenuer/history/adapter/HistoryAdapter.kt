package com.wooriyo.pinmenuer.history.adapter

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
import com.wooriyo.pinmenuer.config.AppProperties
import com.wooriyo.pinmenuer.databinding.ListCallBinding
import com.wooriyo.pinmenuer.databinding.ListOrderBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.OrderHistoryDTO
import com.wooriyo.pinmenuer.order.adapter.OrderDetailAdapter
import com.wooriyo.pinmenuer.util.AppHelper

class HistoryAdapter(val dataSet: ArrayList<OrderHistoryDTO>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var orderCompleteListener: ItemClickListener
    lateinit var callCompleteListener: ItemClickListener
    lateinit var deleteListener: ItemClickListener
    lateinit var printClickListener: ItemClickListener

    fun setOnOrderCompleteListener(orderCompleteListener: ItemClickListener) {
        this.orderCompleteListener = orderCompleteListener
    }

    fun setOnDeleteListener(deleteListener: ItemClickListener) {
        this.deleteListener = deleteListener
    }

    fun setOnPrintClickListener(printClickListener: ItemClickListener) {
        this.printClickListener = printClickListener
    }

    fun setOnCallCompleteListener(callCompleteListener: ItemClickListener) {
        this.callCompleteListener = callCompleteListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val bindingOrder = ListOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val bindingCall = ListCallBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        bindingOrder.rv.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.VERTICAL, false)
        bindingCall.rv.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.VERTICAL, false)

        return if(viewType == AppProperties.VIEW_TYPE_ORDER)
            ViewHolderOrder(parent.context, bindingOrder, orderCompleteListener, deleteListener, printClickListener)
        else
            ViewHolderCall(bindingCall, callCompleteListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            AppProperties.VIEW_TYPE_ORDER -> {
                holder as ViewHolderOrder
                holder.bind(dataSet[position])
            }
            AppProperties.VIEW_TYPE_CALL -> {
                holder as ViewHolderCall
                holder.bind(dataSet[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(dataSet[position].ordType == 1) AppProperties.VIEW_TYPE_ORDER else AppProperties.VIEW_TYPE_CALL
    }

    class ViewHolderOrder(val context: Context, val binding:ListOrderBinding, val completeListener: ItemClickListener, val deleteListener: ItemClickListener, val printClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: OrderHistoryDTO) {
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

    class ViewHolderCall(val binding: ListCallBinding, val callCompleteListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind (data: OrderHistoryDTO) {
            binding.run {
                rv.adapter = HisCallAdapter(data.olist)

                tableNo.text = data.tableNo
                regdt.text = data.regdt

                if(data.iscompleted == 1) {
                    tableNo.setBackgroundColor(Color.parseColor("#E0E0E0"))
                    done.visibility = View.VISIBLE
                    complete.isEnabled = false
                }else {
                    tableNo.setBackgroundResource(R.color.main)
                    done.visibility = View.GONE
                    complete.isEnabled = true
                }

                complete.setOnClickListener { callCompleteListener.onItemClick(absoluteAdapterPosition) }
            }
        }
    }
}