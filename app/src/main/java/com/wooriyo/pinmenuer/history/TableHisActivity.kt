package com.wooriyo.pinmenuer.history

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sewoo.jpos.command.ESCPOSConst
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.call.adapter.CallListAdapter
import com.wooriyo.pinmenuer.common.ConfirmDialog
import com.wooriyo.pinmenuer.common.NoticeDialog
import com.wooriyo.pinmenuer.config.AppProperties
import com.wooriyo.pinmenuer.databinding.ActivityByHistoryBinding
import com.wooriyo.pinmenuer.history.adapter.HistoryAdapter
import com.wooriyo.pinmenuer.history.adapter.ReservationAdapter
import com.wooriyo.pinmenuer.history.dialog.SetTableNoDialog
import com.wooriyo.pinmenuer.listener.DialogListener
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.CallHistoryDTO
import com.wooriyo.pinmenuer.model.CallListDTO
import com.wooriyo.pinmenuer.model.OrderHistoryDTO
import com.wooriyo.pinmenuer.model.OrderListDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.order.adapter.OrderAdapter
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper.Companion.getPrint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TableHisActivity: BaseActivity() {
    lateinit var binding: ActivityByHistoryBinding

    private val totalList = ArrayList<OrderHistoryDTO>()
    val totalAdapter = HistoryAdapter(totalList)

    private val orderList = ArrayList<OrderHistoryDTO>()
    val orderAdapter = OrderAdapter(orderList)

    private val reservList = ArrayList<OrderHistoryDTO>()
    val reservAdapter = ReservationAdapter(reservList)

    private val callList = ArrayList<CallHistoryDTO>()
    val callAdapter = CallListAdapter(callList)

    private val completeList = ArrayList<OrderHistoryDTO>()
    val completeAdapter = HistoryAdapter(completeList)

    // 프린트 관련 변수
    var hyphen = StringBuilder()    // 하이픈
    var hyphen_num = AppProperties.HYPHEN_NUM   // 하이픈 개수
    var font_size = AppProperties.FONT_SIZE

    var selText: TextView?= null

    var tableNo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityByHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tableNo = intent.getStringExtra("tableNo") ?: ""

        // 예약 테이블은 주문, 호출 탭 가리기 >> 텍스트로 예외처리 하는 것이 위험해서 추후 api 부터 수정 예정
        if(tableNo == "예약") {
            binding.tabOrder.visibility = View.GONE
            binding.tabCall.visibility = View.GONE
        }

        for (i in 1..hyphen_num) {
            hyphen.append("-")
        }

        selectTab(binding.tvTotal)

        setAdapterListener(totalAdapter, totalList)
        setAdapterListener(completeAdapter, completeList)
        setOrderAdapter()
        setReservAdapter()
        setCallAdapter()

        binding.btnClear.visibility = View.GONE

        binding.rv.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rv.adapter = totalAdapter

        binding.back.setOnClickListener { finish() }

        binding.tabTotal.setOnClickListener {
            selectTab(binding.tvTotal)
            binding.rv.adapter = totalAdapter
            getTotalList()
        }

        binding.tabOrder.setOnClickListener {
            selectTab(binding.tvOrder)
            binding.rv.adapter = orderAdapter
            getOrderList()
            binding.newOrd.visibility = View.INVISIBLE
        }

        binding.tabReserv.setOnClickListener {
            selectTab(binding.tvReserv)
            binding.rv.adapter = reservAdapter
            getReservList()
            binding.newReserv.visibility = View.INVISIBLE
        }

        binding.tabCall.setOnClickListener {
            selectTab(binding.tvCall)
            binding.rv.adapter = callAdapter
            getCallList()
            binding.newCall.visibility = View.INVISIBLE
        }

        binding.tabComplete.setOnClickListener {
            selectTab(binding.tvCmplt)
            binding.rv.adapter = completeAdapter
            getCompletedList()
        }
    }

    override fun onResume() {
        super.onResume()
        reload()
    }

    fun selectTab(tv: TextView) {
        if(selText != tv) {
            selText?.setTextColor(Color.WHITE)
            selText?.setTypeface(null, Typeface.NORMAL)
            tv.setTextColor(ContextCompat.getColor(mActivity, R.color.main))
            tv.setTypeface(tv.typeface, Typeface.BOLD)
            selText = tv
        }
    }

    fun reload() {
        when(selText) {
            binding.tvTotal -> getTotalList()
            binding.tvOrder -> getOrderList()
            binding.tvReserv -> getReservList()
            binding.tvCall -> getCallList()
            binding.tvCmplt -> getCompletedList()
        }
    }

    fun newOrder() {
        runOnUiThread{
            when (selText) {
                binding.tvOrder -> getOrderList()
                binding.tvTotal -> getTotalList()
                else -> binding.newOrd.visibility = View.VISIBLE
            }
        }
    }

    fun newReservation() {
        runOnUiThread{
            when (selText) {
                binding.tvReserv -> getReservList()
                binding.tvTotal -> getTotalList()
                else -> binding.newReserv.visibility = View.VISIBLE
            }
        }
    }

    fun newCall() {
        runOnUiThread {
            when (selText) {
                binding.tvCall -> getCallList()
                binding.tvTotal -> getTotalList()
                else -> binding.newCall.visibility = View.VISIBLE
            }
        }
    }

    private fun setAdapterListener(adapter: HistoryAdapter, list: ArrayList<OrderHistoryDTO>) {
        adapter.setOnOrderCompleteListener(object : ItemClickListener {
            override fun onItemClick(position: Int) {
                super.onItemClick(position)

                if(list[position].iscompleted == 0) {
                    showCompleteDialog("주문") { completeOrder(list[position].idx, 1) }
                }else {
                    completeOrder(list[position].idx, 0)
                }
            }
        })

        adapter.setOnConfirmListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                confirmReservation(position)
            }
        })

        adapter.setOnDeleteListener(object: ItemClickListener {
            override fun onItemClick(position: Int) {
                NoticeDialog(
                    mActivity,
                    getString(R.string.btn_delete),
                    getString(R.string.dialog_delete_order)
                ) { deleteOrder(list[position].idx) }.show()
            }
        })

        adapter.setOnPrintClickListener(object: ItemClickListener {
            override fun onItemClick(position: Int) {print(list[position])}
        })

        adapter.setOnTableNoListener(object: ItemClickListener{
            override fun onItemClick(position: Int) {
                SetTableNoDialog(
                    list[position].idx,
                    object : DialogListener {
                        override fun onTableNoSet(tableNo: String) {
                            list[position].tableNo = tableNo
                            adapter.notifyItemChanged(position)
                        }
                    }).show(supportFragmentManager, "SetTableNoDialog")
            }
        })

        adapter.setOnCallCompleteListener(object : ItemClickListener {
            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                showCompleteDialog("호출") { completeCall(list[position].idx) }
            }
        })

        adapter.setOnCallDeleteListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                NoticeDialog(
                    mActivity,
                    getString(R.string.btn_delete),
                    getString(R.string.dialog_delete_order)
                ) { deleteCall(list[position].idx) }.show()
            }
        })
    }

    fun setOrderAdapter() {
        orderAdapter.setOnCompleteListener(object : ItemClickListener {
            override fun onItemClick(position: Int) {
                super.onItemClick(position)

                if(orderList[position].iscompleted == 0) {
                    showCompleteDialog("주문") { completeOrder(orderList[position].idx, 1) }
                }else {
                    completeOrder(orderList[position].idx, 0)
                }
            }
        })

        orderAdapter.setOnConfirmListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                confirmReservation(position)
            }
        })

        orderAdapter.setOnDeleteListener(object: ItemClickListener {
            override fun onItemClick(position: Int) {
                NoticeDialog(
                    mActivity,
                    getString(R.string.btn_delete),
                    getString(R.string.dialog_delete_order)
                ) { deleteOrder(orderList[position].idx) }.show()
            }
        })

        orderAdapter.setOnPrintClickListener(object: ItemClickListener {
            override fun onItemClick(position: Int) {print(orderList[position])}
        })

        orderAdapter.setOnTableNoListener(object: ItemClickListener{
            override fun onItemClick(position: Int) {
                SetTableNoDialog(
                    orderList[position].idx,
                    object : DialogListener{
                        override fun onTableNoSet(tableNo: String) {
                            orderList[position].tableNo = tableNo
                            orderAdapter.notifyItemChanged(position)
                        }
                    }).show(supportFragmentManager, "SetTableNoDialog")
            }
        })
    }

    fun setReservAdapter() {
        reservAdapter.setOnCompleteListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                super.onItemClick(position)

                if(reservList[position].iscompleted == 0) {
                    showCompleteDialog("주문") { completeOrder(reservList[position].idx, 1) }
                }else {
                    completeOrder(reservList[position].idx, 0)
                }
            }
        })

        reservAdapter.setOnConfirmListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                confirmReservation(position)
            }
        })

        reservAdapter.setOnDeleteListener(object:ItemClickListener{
            override fun onItemClick(position: Int) {
                NoticeDialog(
                    mActivity,
                    getString(R.string.btn_delete),
                    getString(R.string.dialog_delete_order)
                ) { deleteOrder(reservList[position].idx) }.show()
            }
        })

        reservAdapter.setOnPrintClickListener(object:ItemClickListener{
            override fun onItemClick(position: Int) {print(reservList[position])}
        })

        reservAdapter.setOnTableNoListener(object: ItemClickListener{
            override fun onItemClick(position: Int) {
                SetTableNoDialog(
                    reservList[position].idx,
                    object : DialogListener{
                        override fun onTableNoSet(tableNo: String) {
                            reservList[position].tableNo = tableNo
                            reservAdapter.notifyItemChanged(position)
                        }
                    }).show(supportFragmentManager, "SetTableNoDialog")
            }
        })
    }

    fun setCallAdapter() {
        callAdapter.setOnItemClickListener(object : ItemClickListener {
            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                showCompleteDialog("호출") { completeCall(callList[position].idx) }
            }
        })
        callAdapter.setOnDeleteListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                NoticeDialog(
                    mActivity,
                    getString(R.string.btn_delete),
                    getString(R.string.dialog_delete_order)
                ) { deleteCall(callList[position].idx) }.show()
            }
        })
    }

    fun showCompleteDialog(type: String, event: View.OnClickListener) {
        val completeDialog = ConfirmDialog("", String.format(getString(R.string.dialog_complete), type), getString(R.string.btn_complete), event)
        completeDialog.show(supportFragmentManager, "CompleteDialog")
    }

    // 전체 목록 조회
    private fun getTotalList() {
        ApiClient.service.getTableTotalList(MyApplication.useridx, MyApplication.storeidx, tableNo)
            .enqueue(object: Callback<OrderListDTO> {
            override fun onResponse(call: Call<OrderListDTO>, response: Response<OrderListDTO>) {
                Log.d(TAG, "전체 내역 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return

                if(result.status == 1) {
                    totalList.clear()
                    totalList.addAll(result.orderlist)

                    if(totalList.isEmpty()) {
                        binding.empty.visibility = View.VISIBLE
                        binding.rv.visibility = View.GONE
                    }else {
                        binding.empty.visibility = View.GONE
                        binding.rv.visibility = View.VISIBLE
                        totalAdapter.notifyDataSetChanged()
                    }
                }else Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<OrderListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "전체 내역 조회 오류 > $t")
                Log.d(TAG, "전체 내역 조회 오류 > ${call.request()}")
            }
        })
    }

    // 주문 목록 조회
    fun getOrderList() {
        ApiClient.service.getTableOrderList(MyApplication.useridx, MyApplication.storeidx, tableNo).enqueue(object:
            Callback<OrderListDTO> {
            override fun onResponse(call: Call<OrderListDTO>, response: Response<OrderListDTO>) {
                Log.d(TAG, "주문 목록 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null) {
                    when(result.status){
                        1 -> {
                            orderList.clear()
                            orderList.addAll(result.orderlist)

                            if(orderList.isEmpty()) {
                                binding.empty.visibility = View.VISIBLE
                                binding.rv.visibility = View.GONE
                            }else {
                                binding.empty.visibility = View.GONE
                                binding.rv.visibility = View.VISIBLE
                                orderAdapter.notifyDataSetChanged()
                            }
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<OrderListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "주문 목록 조회 오류 > $t")
                Log.d(TAG, "주문 목록 조회 오류 > ${call.request()}")
            }
        })
    }

    // 예약 목록 조회
    fun getReservList() {
        ApiClient.service.getReservList(MyApplication.useridx, MyApplication.storeidx).enqueue(object: Callback<OrderListDTO> {
            override fun onResponse(call: Call<OrderListDTO>, response: Response<OrderListDTO>) {
                Log.d(TAG, "예약 목록 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body()
                if(result != null) {
                    when(result.status){
                        1 -> {
                            reservList.clear()
                            reservList.addAll(result.orderlist)

                            if(reservList.isEmpty()) {
                                binding.empty.visibility = View.VISIBLE
                                binding.rv.visibility = View.GONE
                            }else {
                                binding.empty.visibility = View.GONE
                                binding.rv.visibility = View.VISIBLE
                                reservAdapter.notifyDataSetChanged()
                            }
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<OrderListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "예약 목록 조회 오류 > $t")
                Log.d(TAG, "예약 목록 조회 오류 > ${call.request()}")
            }
        })
    }

    // 호출 리스트 (히스토리) 조회
    fun getCallList() {
        ApiClient.service.getTableCallList(MyApplication.useridx, MyApplication.storeidx, tableNo)
            .enqueue(object: Callback<CallListDTO> {
            override fun onResponse(call: Call<CallListDTO>, response: Response<CallListDTO>) {
                Log.d(TAG, "호출 목록 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return

                if(result.status == 1){
                    callList.clear()
                    callList.addAll(result.callList)

                    if(callList.isEmpty()) {
                        binding.empty.visibility = View.VISIBLE
                        binding.rv.visibility = View.GONE
                    }else {
                        binding.empty.visibility = View.GONE
                        binding.rv.visibility = View.VISIBLE
                        callAdapter.notifyDataSetChanged()
                    }
                } else
                    Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()

            }
            override fun onFailure(call: Call<CallListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "호출 목록 조회 오류 > $t")
                Log.d(TAG, "호출 목록 조회 오류 > ${call.request()}")
            }
        })
    }

    fun getCompletedList() {
        ApiClient.service.getTableCompletedList(MyApplication.useridx, MyApplication.storeidx, tableNo)
            .enqueue(object : Callback<OrderListDTO> {
            override fun onResponse(call: Call<OrderListDTO>, response: Response<OrderListDTO>) {
                Log.d(TAG, "완료 목록 조회 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                if(result.status == 1) {
                    completeList.clear()
                    completeList.addAll(result.orderlist)

                    if(completeList.isEmpty()) {
                        binding.empty.visibility = View.VISIBLE
                        binding.rv.visibility = View.GONE
                    }else {
                        binding.empty.visibility = View.GONE
                        binding.rv.visibility = View.VISIBLE
                        completeAdapter.notifyDataSetChanged()
                    }
                }else
                    Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<OrderListDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "완료 목록 조회 오류 > $t")
                Log.d(TAG, "완료 목록 조회 오류 > ${call.request()}")
            }
        })
    }

    // 주문 완료 처리
    fun completeOrder(idx: Int, isCompleted: Int) {
        val status = if(isCompleted == 1) "Y" else "N"
        ApiClient.service.udtComplete(MyApplication.storeidx, idx, status)
            .enqueue(object:Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "주문 완료 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status){
                        1 -> {
                            Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            reload()
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "주문 완료 실패 > $t")
                    Log.d(TAG, "주문 완료 실패 > ${call.request()}")
                }
            })
    }

    // 호출 완료 처리
    fun completeCall(idx: Int) {
        ApiClient.service.completeCall(MyApplication.storeidx, idx, "Y").enqueue(object:Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "호출 완료 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status){
                    1 -> {
                        Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        reload()
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "호출 완료 실패 > $t")
                Log.d(TAG, "호출 완료 실패 오류 > ${call.request()}")
            }
        })
    }

    // 주문 삭제
    fun deleteOrder(idx: Int) {
        ApiClient.service.deleteOrder(MyApplication.storeidx, idx).enqueue(object:Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "주문 삭제 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status){
                    1 -> {
                        Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        reload()
//                        orderList.removeAt(position)
//                        orderAdapter.notifyItemRemoved(position)
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "주문 삭제 실패 > $t")
                Log.d(TAG, "주문 삭제 실패 > ${call.request()}")
            }
        })
    }

    // 호촐 삭제
    fun deleteCall(idx: Int) {
        ApiClient.service.deleteCall(MyApplication.storeidx, idx).enqueue(object:Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "호출 삭제 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status){
                    1 -> {
                        Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        reload()
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "호출 삭제 실패 > $t")
                Log.d(TAG, "호출 삭제 실패 > ${call.request()}")
            }
        })
    }

    // 예약 확인 처리
    fun confirmReservation(position: Int) {
        ApiClient.service.confirmReservation(
            MyApplication.useridx,
            MyApplication.storeidx, orderList[position].idx)
            .enqueue(object:Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "예약 확인 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status){
                        1 -> {
                            Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                            orderList[position].isreser = 1
                            orderAdapter.notifyItemChanged(position)
                        }
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "예약 확인 실패 > $t")
                    Log.d(TAG, "예약 확인 실패 > ${call.request()}")
                }
            })
    }

    fun print(order: OrderHistoryDTO) {
        val pOrderDt = order.regdt
        val pTableNo = order.tableNo
        val pOrderNo = order.ordcode

        MyApplication.escposPrinter.printAndroidFont(
            MyApplication.store.name,
            AppProperties.FONT_WIDTH,
            AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
        MyApplication.escposPrinter.printAndroidFont("주문날짜 : $pOrderDt",
            AppProperties.FONT_WIDTH,
            AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
        MyApplication.escposPrinter.printAndroidFont("주문번호 : $pOrderNo",
            AppProperties.FONT_WIDTH,
            AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
        MyApplication.escposPrinter.printAndroidFont("테이블번호 : $pTableNo",
            AppProperties.FONT_WIDTH,
            AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
        MyApplication.escposPrinter.printAndroidFont(
            AppProperties.TITLE_MENU,
            AppProperties.FONT_WIDTH,
            AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
        MyApplication.escposPrinter.printAndroidFont(hyphen.toString(),
            AppProperties.FONT_WIDTH, font_size, ESCPOSConst.LK_ALIGNMENT_LEFT)

        order.olist.forEach {
            val pOrder = getPrint(it)
            MyApplication.escposPrinter.printAndroidFont(pOrder,
                AppProperties.FONT_WIDTH, font_size, ESCPOSConst.LK_ALIGNMENT_LEFT)
        }

        if(order.reserType > 0 && order.rlist.isNotEmpty()) {
            val reserv = order.rlist[0]

            MyApplication.escposPrinter.printAndroidFont("전화번호 ${reserv.tel}",
                AppProperties.FONT_WIDTH,
                AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
            MyApplication.escposPrinter.printAndroidFont("예약자명 ${reserv.name}",
                AppProperties.FONT_WIDTH,
                AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
            MyApplication.escposPrinter.printAndroidFont("요청사항",
                AppProperties.FONT_WIDTH,
                AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
            MyApplication.escposPrinter.printAndroidFont(reserv.memo,
                AppProperties.FONT_WIDTH,
                AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)

            var str = ""
            when(order.reserType) {
                1 -> str = "매장"
                2 -> str = "포장"
            }
            MyApplication.escposPrinter.printAndroidFont(
                String.format(getString(R.string.reserv_date), str),
                AppProperties.FONT_WIDTH,
                AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)

            MyApplication.escposPrinter.printAndroidFont(reserv.reserdt,
                AppProperties.FONT_WIDTH,
                AppProperties.FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
        }

        MyApplication.escposPrinter.lineFeed(4)
        MyApplication.escposPrinter.cutPaper()
    }
}