package com.wooriyo.us.pinmenuer.printer.adapter

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.rt.printerlibrary.enumerate.ConnectStateEnum
import com.sewoo.jpos.command.ESCPOSConst
import com.wooriyo.us.pinmenuer.MyApplication
import com.wooriyo.us.pinmenuer.MyApplication.Companion.bluetoothPort
import com.wooriyo.us.pinmenuer.MyApplication.Companion.escposPrinter
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.common.InfoDialog
import com.wooriyo.us.pinmenuer.config.AppProperties
import com.wooriyo.us.pinmenuer.config.AppProperties.Companion.FONT_BIG
import com.wooriyo.us.pinmenuer.config.AppProperties.Companion.FONT_SMALL
import com.wooriyo.us.pinmenuer.config.AppProperties.Companion.FONT_WIDTH
import com.wooriyo.us.pinmenuer.config.AppProperties.Companion.VIEW_TYPE_ADD
import com.wooriyo.us.pinmenuer.config.AppProperties.Companion.VIEW_TYPE_COM
import com.wooriyo.us.pinmenuer.databinding.ListPrinterAddBinding
import com.wooriyo.us.pinmenuer.databinding.ListPrinterBinding
import com.wooriyo.us.pinmenuer.listener.ItemClickListener
import com.wooriyo.us.pinmenuer.model.PrintDTO
import com.wooriyo.us.pinmenuer.printer.NewConnActivity
import com.wooriyo.us.pinmenuer.printer.SetConnActivity
import com.wooriyo.us.pinmenuer.printer.dialog.DetailPrinterDialog
import com.wooriyo.us.pinmenuer.util.PrinterHelper
import java.io.IOException

class PrinterAdapter(val dataSet: ArrayList<BluetoothDevice>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener

    fun setConnClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListPrinterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val binding_add = ListPrinterAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return if(viewType == VIEW_TYPE_COM) ViewHolder(binding, parent.context) else ViewHolder_add(binding_add, itemClickListener)
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

    class ViewHolder(val binding: ListPrinterBinding, val context: Context): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BluetoothDevice) {
            var img = 0
            var model = ""
            val isSewoo = PrinterHelper.checkSewoo(data)

            if (isSewoo) {
                model = "SEWOO SKL-TS400B"
                img = R.drawable.skl_ts400b
            }else {
                model = "RONGTA RP325"
                img = R.drawable.rp325
            }

            binding.ivPrinter.setImageResource(img)
            binding.model.text = "$model [${data.address}]"
            binding.nick.text = data.alias.toString()

            // 연결 상태에 따라 우측 버튼 및 뷰 변경
            val connStatus =
                if(isSewoo) {
                    MyApplication.bluetoothPort.isConnected && MyApplication.connDev_sewoo == data.address
                }else {
                    //                MyApplication.rtPrinter.getPrinterInterface() != null && MyApplication.rtPrinter.connectState == ConnectStateEnum.Connected && MyApplication.rtPrinter.printerInterface.configObject as BluetoothDevice == data
                    MyApplication.rtPrinter.getPrinterInterface() != null && MyApplication.rtPrinter.connectState == ConnectStateEnum.Connected
                }

            if(connStatus) {
                binding.connNo.visibility = View.INVISIBLE
                binding.connDot.visibility = View.VISIBLE
                binding.connStatus.visibility = View.VISIBLE
                binding.connStatus.text = context.getString(R.string.good)
                binding.btnConn.setBackgroundResource(R.drawable.bg_edittext)
                binding.btnConn.text = context.getString(R.string.printer_clear)

                binding.btnTest.setOnClickListener {
                    try {
                        escposPrinter.printAndroidFont(context.getString(R.string.print_test),
                            FONT_WIDTH,
                            FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
                        escposPrinter.printAndroidFont(context.getString(R.string.print_test),
                            FONT_WIDTH,
                            FONT_BIG, ESCPOSConst.LK_ALIGNMENT_LEFT)
                        escposPrinter.lineFeed(4)
                        escposPrinter.cutPaper()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }else {
                binding.connNo.visibility = View.VISIBLE
                binding.connDot.visibility = View.GONE
                binding.connStatus.visibility = View.GONE
                binding.btnConn.setBackgroundResource(R.drawable.bg_btn_r6_grd)
                binding.btnConn.text = context.getString(R.string.btn_conn)

                binding.btnTest.setOnClickListener {
                    InfoDialog(context, "", context.getString(R.string.dialog_check_conn)).show()
                }
            }

            binding.layout.setOnClickListener {
                DetailPrinterDialog(context, data, img, model).show()
            }

            binding.btnConn.setOnClickListener {
                (context as SetConnActivity).loadingDialog.show(context.supportFragmentManager)
                (context as SetConnActivity).connPos = absoluteAdapterPosition
                if(isSewoo)
                    PrinterHelper.connSewoo(context, MyApplication.remoteDevices[absoluteAdapterPosition])
                else
                    PrinterHelper.connRT(context, MyApplication.remoteDevices[absoluteAdapterPosition])
            }
        }
    }

    class ViewHolder_add(val binding: ListPrinterAddBinding, val itemClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.plus.setOnClickListener {
                itemClickListener.onItemClick(absoluteAdapterPosition)
            }
        }
    }
}