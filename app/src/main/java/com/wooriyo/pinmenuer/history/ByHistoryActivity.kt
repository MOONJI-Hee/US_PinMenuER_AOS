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
import com.wooriyo.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.call.adapter.CallListAdapter
import com.wooriyo.pinmenuer.common.ConfirmDialog
import com.wooriyo.pinmenuer.common.NoticeDialog
import com.wooriyo.pinmenuer.config.AppProperties
import com.wooriyo.pinmenuer.databinding.ActivityByHistoryBinding
import com.wooriyo.pinmenuer.history.adapter.HistoryAdapter
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.CallHistoryDTO
import com.wooriyo.pinmenuer.model.CallListDTO
import com.wooriyo.pinmenuer.model.OrderDTO
import com.wooriyo.pinmenuer.model.OrderHistoryDTO
import com.wooriyo.pinmenuer.model.OrderListDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.order.adapter.OrderAdapter
import com.wooriyo.pinmenuer.order.dialog.ClearDialog
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ByHistoryActivity : BaseActivity() {
    lateinit var binding: ActivityByHistoryBinding

    private val totalList = ArrayList<OrderHistoryDTO>()
    val totalAdapter = HistoryAdapter(totalList)

    private val orderList = ArrayList<OrderHistoryDTO>()
    val orderAdapter = OrderAdapter(orderList)

    private val callList = ArrayList<CallHistoryDTO>()
    val callAdapter = CallListAdapter(callList)

    private val completeList = ArrayList<OrderHistoryDTO>()
    val completeAdapter = HistoryAdapter(completeList)

//    private val posErrList = ArrayList<OrderHistoryDTO>()
//    val posErrAdapter = HistoryAdapter(posErrList)

    // 프린트 관련 변수
    var hyphen = StringBuilder()    // 하이픈
    var hyphen_num = 0              // 하이픈 개수
    var font_size = 0
    var hangul_size = 0.0
    var one_line = 0
    var space = 0

    var selText: TextView ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityByHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 영수증에 들어가는 하이픈 문자열 초기화, 설정값 초기화
        if(MyApplication.store.fontsize == 2) {
            hyphen_num = AppProperties.HYPHEN_NUM_SMALL
            font_size = AppProperties.FONT_SMALL
            hangul_size = AppProperties.HANGUL_SIZE_SMALL
            one_line = AppProperties.ONE_LINE_SMALL
            space = AppProperties.SPACE_SMALL
        }else if(MyApplication.store.fontsize == 1) {
            hyphen_num = AppProperties.HYPHEN_NUM_BIG
            font_size = AppProperties.FONT_BIG
            hangul_size = AppProperties.HANGUL_SIZE_BIG
            one_line = AppProperties.ONE_LINE_BIG
            space = AppProperties.SPACE_BIG
        }
        for (i in 1..hyphen_num) {
            hyphen.append("-")
        }

        selectTab(binding.tvTotal)

        setAdapterListener(totalAdapter, totalList)
        setAdapterListener(completeAdapter, completeList)
        setOrderAdapter()
        setCallAdapter()

        binding.rv.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rv.adapter = totalAdapter

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

//        binding.tabPos.setOnClickListener {
//            binding.rv.adapter = posErrAdapter
//            getPosErrList()
//        }

        binding.btnClear.setOnClickListener {
            ClearDialog({ clearCall() }, { clearOrder() }).show(supportFragmentManager, "ClearDialog")
        }

        binding.back.setOnClickListener { finish() }
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
        adapter.setOnOrderCompleteListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                super.onItemClick(position)

                if(list[position].iscompleted == 0) {
                    showCompleteDialog("주문") { completeOrder(list[position].idx, 1) }
                }else {
                    completeOrder(list[position].idx, 0)
                }
            }
        })

        adapter.setOnDeleteListener(object:ItemClickListener{
            override fun onItemClick(position: Int) {
                NoticeDialog(
                    mActivity,
                    getString(R.string.btn_delete),
                    getString(R.string.dialog_delete_order)
                ) { deleteOrder(list[position].idx) }.show()
            }
        })

        adapter.setOnPrintClickListener(object:ItemClickListener{
            override fun onItemClick(position: Int) {print(totalList[position])}
        })

        adapter.setOnCallCompleteListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                showCompleteDialog("호출") { completeCall(list[position].idx) }
            }
        })
    }

    fun setOrderAdapter() {
        orderAdapter.setOnCompleteListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                super.onItemClick(position)

                if(orderList[position].iscompleted == 0) {
                    showCompleteDialog("주문") { completeOrder(orderList[position].idx, 1) }
                }else {
                    completeOrder(orderList[position].idx, 0)
                }
            }
        })

        orderAdapter.setOnDeleteListener(object:ItemClickListener{
            override fun onItemClick(position: Int) {
                NoticeDialog(
                    mActivity,
                    getString(R.string.btn_delete),
                    getString(R.string.dialog_delete_order)
                ) { deleteOrder(orderList[position].idx) }.show()
            }
        })

        orderAdapter.setOnPrintClickListener(object:ItemClickListener{
            override fun onItemClick(position: Int) {print(orderList[position])}
        })
    }

    fun setCallAdapter() {
        callAdapter.setOnItemClickListener(object : ItemClickListener{
            override fun onItemClick(position: Int) {
                super.onItemClick(position)
                showCompleteDialog("호출") { completeCall(callList[position].idx) }
            }
        })
    }

    fun showCompleteDialog(type: String, event: View.OnClickListener) {
        val completeDialog = ConfirmDialog("", String.format(getString(R.string.dialog_complete), type), getString(R.string.btn_complete), event)
        completeDialog.show(supportFragmentManager, "CompleteDialog")
    }

    // 전체 목록 조회
    private fun getTotalList() {
        ApiClient.service.getTotalList(useridx, storeidx).enqueue(object: Callback<OrderListDTO> {
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
        ApiClient.service.getOrderList(useridx, storeidx).enqueue(object: Callback<OrderListDTO> {
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

    // 호출 리스트 (히스토리) 조회
    fun getCallList() {
        ApiClient.service.getCallHistory(useridx, storeidx).enqueue(object: Callback<CallListDTO> {
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

    // 완료 목록 조회
    fun getCompletedList() {
        ApiClient.service.getCompletedList(useridx, storeidx).enqueue(object : Callback<OrderListDTO>{
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

    // 포스 전송 실패 조회
//    fun getPosErrList() {
//        ApiClient.service.getPosErrorList(useridx, storeidx).enqueue(object: Callback<OrderListDTO>{
//            override fun onResponse(call: Call<OrderListDTO>, response: Response<OrderListDTO>) {
//                Log.d(TAG, "포스 연동 실패 목록 조회 url : $response")
//                if(!response.isSuccessful) return
//
//                val result = response.body() ?: return
//                if(result.status == 1) {
//                    posErrList.clear()
//                    posErrList.addAll(result.orderlist)
//
//                    if(posErrList.isEmpty()) {
//                        binding.empty.visibility = View.VISIBLE
//                        binding.rv.visibility = View.GONE
//                    }else {
//                        binding.empty.visibility = View.GONE
//                        binding.rv.visibility = View.VISIBLE
//                        posErrAdapter.notifyDataSetChanged()
//                    }
//                }else
//                    Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onFailure(call: Call<OrderListDTO>, t: Throwable) {
//                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
//                Log.d(TAG, "포스 연동 실패 목록 조회 오류 > $t")
//                Log.d(TAG, "포스 연동 실패 목록 조회 오류 > ${call.request()}")
//            }
//        })
//    }

    // 주문 초기화
    fun clearOrder() {
        ApiClient.service.clearOrder(useridx, storeidx).enqueue(object:Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "주문 초기화 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                if(result.status == 1){
                    if(selText != binding.tvCall) {
                        reload()
                    }
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "주문 초기화 실패 > $t")
                Log.d(TAG, "주문 초기화 실패 > ${call.request()}")
            }
        })
    }

    // 호출 초기화
    fun clearCall() {
        ApiClient.service.clearCall(useridx, storeidx).enqueue(object:Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "직원호출 초기화 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                if(result.status == 1){
                    if(selText != binding.tvOrder)
                        reload()
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "직원호출 초기화 실패 > $t")
                Log.d(TAG, "직원호출 초기화 실패 > ${call.request()}")
            }
        })
    }

    // 주문 완료 처리
    fun completeOrder(idx: Int, isCompleted: Int) {
        val status = if(isCompleted == 1) "Y" else "N"
        ApiClient.service.udtComplete(storeidx, idx, status)
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
        ApiClient.service.completeCall(storeidx, idx, "Y").enqueue(object:Callback<ResultDTO>{
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
        ApiClient.service.deleteOrder(storeidx, idx).enqueue(object:Callback<ResultDTO>{
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
        MyApplication.escposPrinter.lineFeed(4)
        MyApplication.escposPrinter.cutPaper()
    }

    fun getPrint(ord: OrderDTO) : String {
        var total = 0.0

        val result: StringBuilder = StringBuilder()
        val underline1 = StringBuilder()
        val underline2 = StringBuilder()

        ord.name.forEach {
            if(total < one_line)
                result.append(it)
            else if(total < (one_line * 2))
                underline1.append(it)
            else
                underline2.append(it)

            if(it == ' ') {
                total++
            }else
                total += hangul_size
        }

        val mlength = result.toString().length
        val mHangul = result.toString().replace(" ", "").length
        val mSpace = mlength - mHangul
        val mLine = mHangul * hangul_size + mSpace

        var diff = (one_line - mLine + 0.5).toInt()

        if(MyApplication.store.fontsize == 1) {
            if(ord.gea < 10) {
                diff += 1
                space = 4
            } else if (ord.gea >= 100) {
                space = 1
            }
        }else if(MyApplication.store.fontsize == 2) {
            if(ord.gea < 10) {
                diff += 1
                space += 2
            } else if (ord.gea < 100) {
                space += 1
            }
        }

        for(i in 1..diff) {
            result.append(" ")
        }
        result.append(ord.gea.toString())

        for (i in 1..space) {
            result.append(" ")
        }

        var togo = ""
        when(ord.togotype) {
            1-> togo = "신규"
            2-> togo = "포장"
        }
        result.append(togo)

        if(underline1.toString() != "")
            result.append("\n$underline1")

        if(underline2.toString() != "")
            result.append("\n$underline2")

        return result.toString()
    }
}