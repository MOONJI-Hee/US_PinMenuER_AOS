package com.wooriyo.pinmenuer.history.dialog

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.wooriyo.pinmenuer.BaseDialogFragment
import com.wooriyo.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.DialogSetTableNoBinding
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.history.adapter.TableNoAdapter
import com.wooriyo.pinmenuer.listener.DialogListener
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.model.TableNoDTO
import com.wooriyo.pinmenuer.model.TableNoListDTO
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SetTableNoDialog(val idx: Int, val dialogListener: DialogListener): BaseDialogFragment() {
    lateinit var binding: DialogSetTableNoBinding
    val TAG = context.toString()

    var tableNoList = ArrayList<TableNoDTO>()
    var tableNoAdapter = TableNoAdapter(tableNoList)

    var selpos = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogSetTableNoBinding.inflate(inflater)

        tableNoAdapter.setOnItemClickListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                if(selpos == position) {
                    tableNoList[position].isChecked = false
                    selpos = -1
                } else {
                    if(selpos > -1) {
                        tableNoList[selpos].isChecked = false
                        tableNoAdapter.notifyItemChanged(selpos)
                    }
                    tableNoList[position].isChecked = true
                    selpos = position
                }
            }
        })

        binding.rvtableNo.layoutManager = GridLayoutManager(context, 5)
        binding.rvtableNo.adapter = tableNoAdapter

        binding.close.setOnClickListener{ dismiss() }
        binding.save.setOnClickListener{
            if(selpos < 0) {
                Toast.makeText(context, R.string.msg_no_table_no, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            setTableNo()
        }

        getTableNo()

        return binding.root
    }

    fun getTableNo() {
        ApiClient.service.getTableNo(useridx, storeidx).enqueue(object : Callback<TableNoListDTO>{
            override fun onResponse(call: Call<TableNoListDTO>, response: Response<TableNoListDTO>) {
                Log.d(TAG, "테이블 번호 리스트 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null){
                    when(result.status){
                        1 -> {
                            tableNoList.clear()
                            tableNoList.addAll(result.tableNoList)
                            tableNoAdapter.notifyDataSetChanged()
                        }
                        else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<TableNoListDTO>, t: Throwable) {
                Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "테이블 번호 리스트 조회 실패 >> $t")
                Log.d(TAG, "테이블 번호 리스트 조회 실패 >> ${call.request()}")
            }
        })
    }

    fun setTableNo() {
        val tableNo = tableNoList[selpos].no

        ApiClient.service.udtReservTableNo(useridx, storeidx, idx, tableNo).enqueue(object : retrofit2.Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "테이블 번호 변경 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null){
                    when(result.status){
                        1 -> {
                            Toast.makeText(context, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            dialogListener.onTableNoSet(tableNo)
                            dismiss()
                        }
                        else -> Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(context, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "테이블 번호 변경 실패 >> $t")
                Log.d(TAG, "테이블 번호 변경 실패 >> ${call.request()}")
            }
        })
    }
}