package com.wooriyo.us.pinmenuer.qrcode.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wooriyo.us.pinmenuer.MyApplication
import com.wooriyo.us.pinmenuer.R
import com.wooriyo.us.pinmenuer.common.InfoDialog
import com.wooriyo.us.pinmenuer.config.AppProperties
import com.wooriyo.us.pinmenuer.databinding.ListQrBinding
import com.wooriyo.us.pinmenuer.databinding.ListQrPlusBinding
import com.wooriyo.us.pinmenuer.listener.ItemClickListener
import com.wooriyo.us.pinmenuer.model.QrDTO
import com.wooriyo.us.pinmenuer.qrcode.SetQrcodeActivity
import com.wooriyo.us.pinmenuer.qrcode.dialog.QrDetailDialog
import com.wooriyo.us.pinmenuer.util.AppHelper

class QrAdapter(val dataSet: ArrayList<QrDTO>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var postPayClickListener: ItemClickListener
    lateinit var buseClickListener: ItemClickListener
    lateinit var addClickListener: View.OnClickListener

    var qrCnt = 0

    fun setOnPostPayClickListener(postPayClickListener: ItemClickListener) {
        this.postPayClickListener = postPayClickListener
    }

    fun setOnReserBuseClickListener(buseClickListener: ItemClickListener) {
        this.buseClickListener = buseClickListener
    }

    fun setOnAddClickListener(addClickListener: View.OnClickListener) {
        this.addClickListener = addClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListQrBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val bindingAdd = ListQrPlusBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return if(viewType == AppProperties.VIEW_TYPE_COM) ViewHolder(binding, parent.context, postPayClickListener, buseClickListener)
                else ViewHolder_add(bindingAdd, parent.context, addClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            AppProperties.VIEW_TYPE_COM -> {
                holder as ViewHolder
                holder.bind(dataSet[position-1], qrCnt)
            }
            AppProperties.VIEW_TYPE_ADD -> {
                holder as ViewHolder_add
                holder.bind()
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0) AppProperties.VIEW_TYPE_ADD else AppProperties.VIEW_TYPE_COM
    }

    fun setQrCount(qrCnt: Int) {
        this.qrCnt = qrCnt
    }

    class ViewHolder(val binding: ListQrBinding, val context: Context,
                     val postPayClickListener: ItemClickListener,
                     val buseClickListener: ItemClickListener
    ):RecyclerView.ViewHolder(binding.root) {
        fun bind(data: QrDTO, qrCnt: Int) {
            binding.tableNo.text = data.tableNo

            Glide.with(context)
                .load(data.filePath)
                .into(binding.ivQr)

            if(data.seq == 0) {
                binding.seq.text = context.getString(R.string.qr_reserv)
                binding.tvBuse.text = context.getString(R.string.use_status)

                binding.buse.isChecked = data.buse == "Y"

                binding.buse.setOnClickListener {
                    it as CheckBox
                    buseClickListener.onQrClick(1, it.isChecked)
                }

            } else {
                binding.seq.text = String.format(context.getString(R.string.qr_cnt), AppHelper.intToString(absoluteAdapterPosition - 1))
                binding.tvBuse.text = context.getString(R.string.qr_post_pay)

                binding.buse.isChecked = data.qrbuse == "Y"

                binding.buse.setOnClickListener {
                    it as CheckBox
                    if(MyApplication.store.paytype == 2) {
                        postPayClickListener.onQrClick(absoluteAdapterPosition - 1, it.isChecked)
                    }else {
                        it.isChecked = false
                        InfoDialog(context, "", context.getString(R.string.dialog_no_business)).show()
                    }
                }

                if(qrCnt < absoluteAdapterPosition - 1) {
                    binding.disable.visibility = View.VISIBLE
                }else
                    binding.disable.visibility = View.GONE
            }

            binding.able.setOnClickListener{
                val qrDetailDialog = QrDetailDialog(absoluteAdapterPosition - 1, data)
                qrDetailDialog.show((context as SetQrcodeActivity).supportFragmentManager, "QrDetailDialog")
            }

            binding.disable.setOnClickListener {
                InfoDialog(context, "", context.getString(R.string.dialog_disable_qr)).show()
            }
        }
    }

    class ViewHolder_add(val binding: ListQrPlusBinding, val context: Context, val addClickListener: View.OnClickListener
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.plus.setOnClickListener{
                addClickListener.onClick(it)
            }
        }
    }
}