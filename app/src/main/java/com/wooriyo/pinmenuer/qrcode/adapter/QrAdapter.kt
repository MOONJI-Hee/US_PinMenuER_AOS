package com.wooriyo.pinmenuer.qrcode.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.common.InfoDialog
import com.wooriyo.pinmenuer.config.AppProperties
import com.wooriyo.pinmenuer.databinding.ListQrBinding
import com.wooriyo.pinmenuer.databinding.ListQrPlusBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.QrDTO
import com.wooriyo.pinmenuer.qrcode.SetQrcodeActivity
import com.wooriyo.pinmenuer.qrcode.dialog.QrDetailDialog

class QrAdapter(val dataSet: ArrayList<QrDTO>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var postPayClickListener: ItemClickListener

    var qrCnt = 0

    fun setOnPostPayClickListener(postPayClickListener: ItemClickListener) {
        this.postPayClickListener = postPayClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListQrBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val bindingAdd = ListQrPlusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return if(viewType == AppProperties.VIEW_TYPE_COM) ViewHolder(binding, parent.context) else ViewHolder_add(bindingAdd, parent.context, postPayClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            AppProperties.VIEW_TYPE_COM -> {
                holder as ViewHolder
                holder.bind(dataSet[position], qrCnt, postPayClickListener)
            }
            AppProperties.VIEW_TYPE_ADD -> {
                holder as ViewHolder_add
                holder.bind()
            }
        }
    }

    override fun getItemCount(): Int {
        return if(dataSet.size < qrCnt) dataSet.size + 1 else dataSet.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == dataSet.size) AppProperties.VIEW_TYPE_ADD else AppProperties.VIEW_TYPE_COM
    }

    fun setQrCount(qrCnt: Int) {
        this.qrCnt = qrCnt
    }

    class ViewHolder(val binding: ListQrBinding, val context: Context):RecyclerView.ViewHolder(binding.root) {
        fun bind(data: QrDTO, qrCnt: Int, postPayClickListener: ItemClickListener) {
            binding.tableNo.text = data.tableNo
            binding.seq.text = String.format(context.getString( R.string.qr_cnt), adapterPosition+1)

            Glide.with(context)
                .load(data.filePath)
                .into(binding.ivQr)

            if(qrCnt < absoluteAdapterPosition+1) {
                binding.disable.visibility = View.VISIBLE
            }else
                binding.disable.visibility = View.GONE

            binding.postPay.isChecked = data.qrbuse == "Y"

            binding.able.setOnClickListener{
                val qrDetailDialog = QrDetailDialog(absoluteAdapterPosition + 1, data)
                qrDetailDialog.setOnPostPayClickListener(postPayClickListener)
                qrDetailDialog.show((context as SetQrcodeActivity).supportFragmentManager, "QrDetailDialog")
            }

            binding.disable.setOnClickListener {
                InfoDialog(context, "", context.getString(R.string.dialog_disable_qr)).show()
            }

            binding.postPay.setOnClickListener {
                it as CheckBox
                if(MyApplication.store.paytype == 2) {
                    postPayClickListener.onQrClick(absoluteAdapterPosition, it.isChecked)
                }else {
                    it.isChecked = false
                    InfoDialog(context, "", context.getString(R.string.dialog_no_business)).show()
                }
            }
        }
    }

    class ViewHolder_add(val binding: ListQrPlusBinding, val context: Context, val postPayClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.plus.setOnClickListener{
                val qrDetailDialog = QrDetailDialog(absoluteAdapterPosition + 1, null)
                qrDetailDialog.setOnPostPayClickListener(postPayClickListener)
                qrDetailDialog.show((context as SetQrcodeActivity).supportFragmentManager, "QrAddDialog")
            }
        }
    }
}