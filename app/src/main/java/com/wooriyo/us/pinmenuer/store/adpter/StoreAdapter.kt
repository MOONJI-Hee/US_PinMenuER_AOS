package com.wooriyo.us.pinmenuer.store.adpter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.us.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.us.pinmenuer.config.AppProperties.Companion.VIEW_TYPE_ADD
import com.wooriyo.us.pinmenuer.config.AppProperties.Companion.VIEW_TYPE_COM
import com.wooriyo.us.pinmenuer.config.AppProperties.Companion.VIEW_TYPE_EMPTY
import com.wooriyo.pinmenuer.databinding.ListStoreAddBinding
import com.wooriyo.pinmenuer.databinding.ListStoreBinding
import com.wooriyo.pinmenuer.databinding.ListStoreEmptyBinding
import com.wooriyo.us.pinmenuer.listener.ItemClickListener
import com.wooriyo.us.pinmenuer.model.StoreDTO
import com.wooriyo.us.pinmenuer.store.StoreMenuActivity
import com.wooriyo.us.pinmenuer.store.StoreSetActivity
import com.wooriyo.us.pinmenuer.util.AppHelper.Companion.dateNowCompare

class StoreAdapter(val dataSet: ArrayList<StoreDTO>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener

    var empty = 0

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun setEmptyBox(empty : Int) {
        this.empty = empty
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val binding_add = ListStoreAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val binding_empty = ListStoreEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return if(viewType == VIEW_TYPE_ADD) AddViewHolder(parent.context, binding_add) else if(viewType == VIEW_TYPE_EMPTY) EmptyViewHolder(binding_empty) else ViewHolder(parent.context, binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == VIEW_TYPE_COM)
            (holder as ViewHolder).bind(dataSet[position])
        else if (getItemViewType(position) == VIEW_TYPE_ADD)
            (holder as AddViewHolder).bind()
    }

    override fun getItemCount(): Int {
        return dataSet.size + empty
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == dataSet.size) VIEW_TYPE_ADD else if(position > dataSet.size) VIEW_TYPE_EMPTY else VIEW_TYPE_COM
    }

    class ViewHolder(val context: Context, val binding: ListStoreBinding, val itemClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoreDTO) {
            // 유료기능 사용 가능 여부 > 요금제 사용 중
            val usePay = data.payuse == "Y" && dateNowCompare(data.endDate)

            binding.run {
                storeName.text = data.name

                if(data.subname.isNullOrEmpty())
                    storeName2.visibility = View.GONE
                else {
                    storeName2.visibility = View.VISIBLE
                    storeName2.text = data.subname
                }

                if(usePay || data.paytype == 4) {
                    storeName.isEnabled = true
                    storeName2.isEnabled = true
                    storePayInfo.visibility = View.VISIBLE
                    storePayDt.visibility = View.VISIBLE
                    storePayNo.visibility = View.GONE

                    storePayDt.text = data.paydate.split(" ")[0].replace("-", ".")

                    storeMenu.setOnClickListener {
                        val intent = Intent(context, StoreMenuActivity::class.java)
                        intent.putExtra("usePay", usePay)
                        itemClickListener.onStoreClick(data, intent, usePay)
                    }
                }else {
                    storeName.isEnabled = false
                    storeName2.isEnabled = false
                    storePayInfo.visibility = View.INVISIBLE
                    storePayDt.visibility = View.INVISIBLE
                    storePayNo.visibility = View.VISIBLE

                    storeMenu.setOnClickListener {
                        Toast.makeText(context, R.string.msg_no_pay, Toast.LENGTH_SHORT).show()
                    }
                }

                storeUdt.setOnClickListener{
                    MyApplication.storeidx = data.idx
                    MyApplication.store = data
                    val intent = Intent(context, StoreSetActivity::class.java)
                    intent.putExtra("type", 2)  // 매장 수정 > 2
                    context.startActivity(intent)
                }
            }

            if(data.paydate.isNotEmpty() && data.payuse == "N") {
                AlertDialog.Builder(context)
                    .setTitle(R.string.dialog_notice)
                    .setMessage("${data.name}\n이용기간이 만료되었습니다.\n정기결제 후 이용을 부탁드립니다.")
                    .setPositiveButton(R.string.confirm) { dialog, _ -> dialog.dismiss()}
                    .show()
            }
        }
    }

    class AddViewHolder(val context: Context, val binding: ListStoreAddBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.btnPlus.setOnClickListener{
                val intent = Intent(context, StoreSetActivity::class.java)
                MyApplication.setStoreDTO()
                intent.putExtra("type", 1)
                context.startActivity(intent)
            }
        }
    }

    class EmptyViewHolder(val binding: ListStoreEmptyBinding): RecyclerView.ViewHolder(binding.root) {}
}