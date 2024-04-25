package com.wooriyo.pinmenuer.menu.adpter

import android.content.ClipData.Item
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ListCateEditBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.menu.dialog.CategoryDialog
import com.wooriyo.pinmenuer.model.CategoryDTO
import java.util.Collections

class CateEditAdapter(val dataSet: ArrayList<CategoryDTO>): RecyclerView.Adapter<CateEditAdapter.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener
    lateinit var itemMoveListener: ItemClickListener

    var flag: Int = 0   // 0 : 등록수정, 1 : 순서변경
    var selPos: Int = -1

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun setOnMoveListener(itemMoveListener: ItemClickListener) {
        this.itemMoveListener = itemMoveListener
    }

    fun setMode(flag: Int) {
        this.flag = flag
        notifyDataSetChanged()
    }

    fun swapData(fromPos: Int, toPos: Int) {
        Collections.swap(dataSet, fromPos, toPos)
        notifyItemMoved(fromPos, toPos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListCateEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])

        if(flag == 1) {
            holder.seqBind(selPos, itemCount-1, itemMoveListener)
            holder.binding.run {
                llCate.setOnClickListener {
                    val beforePos = selPos
                    selPos = position

                    if(beforePos == selPos) {
                        selPos = -1
                        it.setBackgroundResource(R.drawable.bg_r6w)
                        left.visibility = View.GONE
                        right.visibility = View.GONE
                    }else {
                        it.setBackgroundResource(R.drawable.bg_btn_r6_grd)
                        left.visibility = View.VISIBLE
                        right.visibility = View.VISIBLE
                        notifyItemChanged(beforePos)
                    }
                }
            }
        }else {
            holder.binding.run {
                if(position == selPos) {    // 수정 중에 취소를 누르면 이전에 선택되어있던 카테고리를 초기화 하기 위한 코드
                    selPos = -1
                    llCate.setBackgroundResource(R.drawable.bg_r6w)
                    left.visibility = View.GONE
                    right.visibility = View.GONE
                }
                llCate.setOnClickListener { itemClickListener.onItemClick(position) }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getItemId(position: Int): Long {
        return dataSet[position].idx.toLong()
    }

    class ViewHolder(val binding: ListCateEditBinding, val itemClickListener: ItemClickListener): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CategoryDTO) {
            binding.run {
                name.text = data.name
                subName.text = data.subname

                if (data.buse == "N") {
                    name.setTextColor(Color.parseColor("#696969"))
                    subName.setTextColor(Color.parseColor("#696969"))
                } else {
                    name.setTextColor(Color.BLACK)
                    subName.setTextColor(Color.BLACK)
                }
            }
        }
        fun seqBind(selPos: Int, lastIndex: Int, itemMoveListener: ItemClickListener) {
            val position = bindingAdapterPosition
            binding.run {
                if(position != selPos){
                    llCate.setBackgroundResource(R.drawable.bg_r6w)
                    left.visibility = View.GONE
                    right.visibility = View.GONE
                }

                left.setOnClickListener {
                    if(position != 0) {
                        itemMoveListener.onItemMove(position, position-1)
                    }
                }
                right.setOnClickListener {
                    if(position != lastIndex) {
                        itemMoveListener.onItemMove(position, position+1)
                    }
                }
            }
        }
    }
}