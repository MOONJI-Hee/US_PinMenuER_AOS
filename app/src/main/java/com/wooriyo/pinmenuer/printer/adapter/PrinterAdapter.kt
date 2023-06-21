package com.wooriyo.pinmenuer.printer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sewoo.jpos.command.ESCPOSConst
import com.wooriyo.pinmenuer.MyApplication.Companion.bluetoothPort
import com.wooriyo.pinmenuer.MyApplication.Companion.escposPrinter
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.common.NoticeDialog
import com.wooriyo.pinmenuer.config.AppProperties.Companion.FONT_BIG
import com.wooriyo.pinmenuer.config.AppProperties.Companion.FONT_SMALL
import com.wooriyo.pinmenuer.config.AppProperties.Companion.FONT_WIDTH
import com.wooriyo.pinmenuer.config.AppProperties.Companion.VIEW_TYPE_ADD
import com.wooriyo.pinmenuer.config.AppProperties.Companion.VIEW_TYPE_COM
import com.wooriyo.pinmenuer.databinding.ListPrinterAddBinding
import com.wooriyo.pinmenuer.databinding.ListPrinterBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.PrintDTO
import com.wooriyo.pinmenuer.printer.NewConnActivity
import com.wooriyo.pinmenuer.printer.dialog.DetailPrinterDialog
import java.io.IOException

class PrinterAdapter(val dataSet: ArrayList<PrintDTO>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener

    fun setConnClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListPrinterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val binding_add = ListPrinterAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return if(viewType == VIEW_TYPE_COM) ViewHolder(binding, parent.context, itemClickListener) else ViewHolder_add(binding_add, parent.context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            VIEW_TYPE_COM -> {
                holder as ViewHolder
                holder.bind(dataSet[position])
            }
            VIEW_TYPE_ADD -> {
                holder as ViewHolder_add
                holder.bind()
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size+1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == itemCount - 1) VIEW_TYPE_ADD else VIEW_TYPE_COM
    }

    class ViewHolder(val binding: ListPrinterBinding, val context: Context, val itemClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PrintDTO) {
            var cmp = ""
            var img = 0
            when(data.printType) {
                1 -> {
                    cmp = "세우테크"
                    img = R.drawable.skl_ts400b
                }
                2-> {
                    cmp = "세우테크"
                    img = R.drawable.skl_te202
                }
                3 -> {
                    cmp = "GCUBE"
                    img = R.drawable.sam4s
                }
            }

            binding.ivPrinter.setImageResource(img)

            binding.model.text = "$cmp ${data.model}"
            if(data.nick != "") {
                binding.nick.text = data.nick
            }

            // 연결 상태에 따라 우측 버튼 및 뷰 변경
            if(data.connected) {
                binding.connNo.visibility = View.INVISIBLE
                binding.connDot.visibility = View.VISIBLE
                binding.connStatus.visibility = View.VISIBLE
                binding.connStatus.text = context.getString(R.string.good)
                binding.btnConn.setBackgroundResource(R.drawable.bg_edittext)
                binding.btnConn.text = context.getString(R.string.printer_clear)
            }else {
                binding.connNo.visibility = View.VISIBLE
                binding.connDot.visibility = View.GONE
                binding.connStatus.visibility = View.GONE
                binding.btnConn.setBackgroundResource(R.drawable.bg_btn_r6_grd)
                binding.btnConn.text = context.getString(R.string.btn_conn)
            }

            binding.layout.setOnClickListener {
                DetailPrinterDialog(context, data).show()
            }

            binding.btnTest.setOnClickListener {
                if(bluetoothPort.isConnected) {
                    try {
                        escposPrinter.printAndroidFont(context.getString(R.string.print_test), FONT_WIDTH, FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                        escposPrinter.printAndroidFont(context.getString(R.string.print_test), FONT_WIDTH, FONT_BIG, ESCPOSConst.LK_ALIGNMENT_LEFT)
                        escposPrinter.lineFeed(4)
                        escposPrinter.cutPaper()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }else
                    NoticeDialog(context, context.getString(R.string.dialog_check_conn)).show()
            }

            binding.btnConn.setOnClickListener {
                itemClickListener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    class ViewHolder_add(val binding: ListPrinterAddBinding, val context: Context): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.plus.setOnClickListener {
                context.startActivity(Intent(context, NewConnActivity::class.java))
            }
        }
    }
}