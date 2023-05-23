package com.wooriyo.pinmenuer.store.adpter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.config.AppProperties.Companion.VIEW_TYPE_ADD
import com.wooriyo.pinmenuer.config.AppProperties.Companion.VIEW_TYPE_COM
import com.wooriyo.pinmenuer.databinding.ListStoreAddBinding
import com.wooriyo.pinmenuer.databinding.ListStoreBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.StoreDTO
import com.wooriyo.pinmenuer.store.StoreMenuActivity
import com.wooriyo.pinmenuer.store.StoreSetActivity
import com.wooriyo.pinmenuer.util.AppHelper.Companion.dateNowCompare

class StoreAdapter(val dataSet: ArrayList<StoreDTO>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val binding_add = ListStoreAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return if(viewType == VIEW_TYPE_ADD) AddViewHolder(parent.context, binding_add) else ViewHolder(parent.context, binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == VIEW_TYPE_COM)
            (holder as ViewHolder).bind(dataSet[position])
        else
            (holder as AddViewHolder).bind()
    }

    override fun getItemCount(): Int {
        return dataSet.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == itemCount -1) VIEW_TYPE_ADD else VIEW_TYPE_COM
    }

    class ViewHolder(val context: Context, val binding: ListStoreBinding, val itemClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoreDTO) {
            binding.run {
                storeName.text = data.name

                if(data.name2.isNullOrEmpty())
                    storeName2.visibility = View.GONE
                else {
                    storeName2.visibility = View.VISIBLE
                    storeName2.text = data.name2
                }

                if(data.payuse == "Y" && dateNowCompare(data.paydate)) {
                    storeMenu.isEnabled = true
                    storeName.isEnabled = true
                    storeName2.isEnabled = true
                    storeUdt.isEnabled = true
                    storePayInfo.visibility = View.VISIBLE
                    storePayDt.visibility = View.VISIBLE
                    storePayNo.visibility = View.GONE
                }else {
                    storeMenu.isEnabled = false
                    storeName.isEnabled = false
                    storeName2.isEnabled = false
                    storeUdt.isEnabled = false
                    storePayInfo.visibility = View.INVISIBLE
                    storePayDt.visibility = View.INVISIBLE
                    storePayNo.visibility = View.VISIBLE
                }

                storePayDt.text = data.paydate.split(" ")[0].replace("-", ".")

                storeMenu.setOnClickListener {
                    itemClickListener.onStoreClick(data,Intent(context, StoreMenuActivity::class.java))
                }

                storeUdt.setOnClickListener{
                    MyApplication.storeidx = data.idx
                    MyApplication.store = data
                    val intent = Intent(context, StoreSetActivity::class.java)
                    intent.putExtra("type", 2)  // 매장 수정 > 2
                    context.startActivity(intent)
                }
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
}