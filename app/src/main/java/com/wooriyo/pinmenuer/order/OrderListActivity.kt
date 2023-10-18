package com.wooriyo.pinmenuer.order

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.sewoo.jpos.command.ESCPOSConst
import com.sewoo.jpos.printer.ESCPOSPrinter
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication.Companion.store
import com.wooriyo.pinmenuer.MyApplication.Companion.storeidx
import com.wooriyo.pinmenuer.MyApplication.Companion.useridx
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.broadcast.EasyCheckReceiver
import com.wooriyo.pinmenuer.common.ConfirmDialog
import com.wooriyo.pinmenuer.common.NoticeDialog
import com.wooriyo.pinmenuer.config.AppProperties
import com.wooriyo.pinmenuer.config.AppProperties.Companion.FONT_BIG
import com.wooriyo.pinmenuer.config.AppProperties.Companion.FONT_SMALL
import com.wooriyo.pinmenuer.config.AppProperties.Companion.FONT_WIDTH
import com.wooriyo.pinmenuer.config.AppProperties.Companion.HANGUL_SIZE_BIG
import com.wooriyo.pinmenuer.config.AppProperties.Companion.HANGUL_SIZE_SMALL
import com.wooriyo.pinmenuer.config.AppProperties.Companion.ONE_LINE_BIG
import com.wooriyo.pinmenuer.config.AppProperties.Companion.ONE_LINE_SMALL
import com.wooriyo.pinmenuer.config.AppProperties.Companion.SPACE_BIG
import com.wooriyo.pinmenuer.config.AppProperties.Companion.SPACE_SMALL
import com.wooriyo.pinmenuer.config.AppProperties.Companion.TITLE_MENU
import com.wooriyo.pinmenuer.databinding.ActivityOrderListBinding
import com.wooriyo.pinmenuer.listener.DialogListener
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.OrderDTO
import com.wooriyo.pinmenuer.model.OrderHistoryDTO
import com.wooriyo.pinmenuer.model.OrderListDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.order.adapter.OrderAdapter
import com.wooriyo.pinmenuer.order.dialog.ClearDialog
import com.wooriyo.pinmenuer.order.dialog.CompleteDialog
import com.wooriyo.pinmenuer.order.dialog.SelectPayDialog
import com.wooriyo.pinmenuer.payment.NicepayInfoActivity
import com.wooriyo.pinmenuer.payment.PayCardActivity
import com.wooriyo.pinmenuer.payment.QrActivity
import com.wooriyo.pinmenuer.payment.SetPgInfoActivity
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderListActivity : BaseActivity() {
    lateinit var binding: ActivityOrderListBinding
    lateinit var clearDialog: ClearDialog
    lateinit var clearConfirmDialog: NoticeDialog

    val TAG = "OrderListActivity"
    val mActivity = this@OrderListActivity

    val orderList = ArrayList<OrderHistoryDTO>()
    val orderAdapter = OrderAdapter(orderList)

    // 프린트 관련 변수
    val escposPrinter = ESCPOSPrinter()

    var hyphen = StringBuilder()    // 하이픈
    var hyphen_num = 0              // 하이픈 개수
    var font_size = 0
    var hangul_size = 0.0
    var one_line = 0
    var space = 0

    // 결제 관련 변수
    lateinit var receiver : EasyCheckReceiver
    var payPosition = -1

    val paymentCard = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == RESULT_OK) {
            orderList[payPosition].iscompleted = 1
            orderList.sortBy { it.iscompleted }
            orderAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 영수증에 들어가는 하이픈 문자열 초기화, 설정값 초기화
        if(store.fontsize == 2) {
            hyphen_num = AppProperties.HYPHEN_NUM_SMALL
            font_size = FONT_SMALL
            hangul_size = HANGUL_SIZE_SMALL
            one_line = ONE_LINE_SMALL
            space = SPACE_SMALL
        }else if(store.fontsize == 1) {
            hyphen_num = AppProperties.HYPHEN_NUM_BIG
            font_size = FONT_BIG
            hangul_size = HANGUL_SIZE_BIG
            one_line = ONE_LINE_BIG
            space = SPACE_BIG
        }
        for (i in 1..hyphen_num) {
            hyphen.append("-")
        }

        orderAdapter.setOnPayClickListener(object:ItemClickListener{
            override fun onItemClick(position: Int) {
                if(orderList[position].iscompleted == 1) {
                    complete(position, 0, store.popup)
                }else if(orderList[position].paytype == 3) {
                    completeInfoDialog(position)
                }else {
                    val selectPayDialog = SelectPayDialog(mActivity, position)

                    selectPayDialog.setOnQrClickListener(object : ItemClickListener{
                        override fun onQrClick(position: Int, status: Boolean) {
                            super.onQrClick(position, status)
                            if(status) {
                                val intent = Intent(mActivity, QrActivity::class.java)
                                intent.putExtra("ordcode_key", orderList[position].ordcode_key)
                                startActivity(intent)
                            }else {
                                val intent = Intent(mActivity, SetPgInfoActivity::class.java)
                                intent.putExtra("fromOrder", "Y")
                                startActivity(intent)
                            }
                        }
                    })

                    selectPayDialog.setOnCardClickListener(object : ItemClickListener{
                        override fun onItemClick(position: Int) {
                            super.onItemClick(position)
                            selectPayDialog.dismiss()
                            payPosition = position

                            val intent = Intent(mActivity, PayCardActivity::class.java)
                            intent.putExtra("order", orderList[payPosition])
                            paymentCard.launch(intent)
                        }
                    })

                    selectPayDialog.setOnCompleteClickListener(object : ItemClickListener{
                        override fun onItemClick(position: Int) {
                            super.onItemClick(position)
                            completeInfoDialog(position)
                            selectPayDialog.dismiss()
                        }
                    })

                    selectPayDialog.show()
                }
            }
        })

        orderAdapter.setOnDeleteListener(object:ItemClickListener{
            override fun onItemClick(position: Int) {
                NoticeDialog(mActivity,
                    getString(R.string.btn_delete),
                    getString(R.string.dialog_delete_order),
                    View.OnClickListener{ delete(position) }
                ).show()
            }
        })

        orderAdapter.setOnPrintClickListener(object:ItemClickListener{
            override fun onItemClick(position: Int) {print(position)}
        })

        setClearDialog()

        binding.rv.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rv.adapter = orderAdapter

        binding.back.setOnClickListener { finish() }
        binding.icNew.setOnClickListener{
            getOrderList()
            it.visibility = View.GONE
        }
        binding.btnClear.setOnClickListener { clearDialog.show() }
    }

    override fun onResume() {
        super.onResume()
        getOrderList()
    }

    // 초기화 / 초기화 확인 다이얼로그 초기화
    fun setClearDialog() {
        clearDialog = ClearDialog(
            mActivity,
            "order",
            View.OnClickListener {
            clearDialog.dismiss()
            clearConfirmDialog.show()
        })

        clearConfirmDialog = NoticeDialog(
            mActivity,
            getString(R.string.dialog_order_clear_title),
            getString(R.string.dialog_confrim_clear),
            View.OnClickListener { clear() }
        )
    }

    // 완료 처리 안내 다이얼로그
    fun completeInfoDialog(position: Int) {
        when(store.popup) {
            0 -> {
                val dialog = CompleteDialog(mActivity)
                dialog.setOnCompleteListener(object : DialogListener {
                    override fun onComplete(popup: Int) {
                        super.onComplete(popup)
                        complete(position, 1, popup)
                        dialog.dismiss()
                    }
                })
                dialog.show()
            }
            1 ->  {
                complete(position, 1, store.popup)
            }
        }
    }

    // 주문 목록 조회
    fun getOrderList() {
        ApiClient.service.getOrderList(useridx, storeidx).enqueue(object : Callback<OrderListDTO>{
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
//                                orderList.sortBy { it.iscompleted }
                                binding.total.text = result.totalCnt.toString()
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

    // 주문 초기화
    fun clear() {
        ApiClient.service.clearOrder(useridx, storeidx).enqueue(object:Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "주문 초기화 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                if(result.status == 1){
                    getOrderList()
                }
            }
            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "주문 초기화 실패 > $t")
                Log.d(TAG, "주문 초기화 실패 > ${call.request()}")
            }
        })
    }

    // 주문 완료 처리
    fun complete(position: Int, isCompleted: Int, popup: Int) {
        val status = if(isCompleted == 1) "Y" else "N"
        ApiClient.service.udtComplete(storeidx, orderList[position].idx, status, popup)
            .enqueue(object:Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "주문 완료 url : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status){
                        1 -> {
                            Toast.makeText(mActivity, R.string.complete, Toast.LENGTH_SHORT).show()
                            orderList[position].iscompleted = isCompleted
                            //                        orderList.sortBy { it.iscompleted }
                            orderAdapter.notifyItemChanged(position)
                            store.popup = popup
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

    // 주문 삭제
    fun delete(position: Int) {
        ApiClient.service.deleteOrder(storeidx, orderList[position].idx).enqueue(object:Callback<ResultDTO>{
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "주문 삭제 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status){
                    1 -> {
                        Toast.makeText(mActivity, R.string.complete, Toast.LENGTH_SHORT).show()
                        orderList.removeAt(position)
                        orderAdapter.notifyItemRemoved(position)
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

    // 주문 프린트
    fun print(position: Int) {
        val pOrderDt = orderList[position].regdt
        val pTableNo = orderList[position].tableNo
        val pOrderNo = orderList[position].ordcode

        escposPrinter.printAndroidFont(store.name, FONT_WIDTH, FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
        escposPrinter.printAndroidFont("주문날짜 : $pOrderDt", FONT_WIDTH, FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
        escposPrinter.printAndroidFont("주문번호 : $pOrderNo", FONT_WIDTH, FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
        escposPrinter.printAndroidFont("테이블번호 : $pTableNo", FONT_WIDTH, FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
        escposPrinter.printAndroidFont(TITLE_MENU, FONT_WIDTH, FONT_SMALL, ESCPOSConst.LK_ALIGNMENT_LEFT)
        escposPrinter.printAndroidFont(hyphen.toString(), FONT_WIDTH, font_size, ESCPOSConst.LK_ALIGNMENT_LEFT)

        orderList[position].olist.forEach {
            val pOrder = getPrint(it)
            escposPrinter.printAndroidFont(pOrder, FONT_WIDTH, font_size, ESCPOSConst.LK_ALIGNMENT_LEFT)
        }
        escposPrinter.lineFeed(4)
        escposPrinter.cutPaper()
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

        if(store.fontsize == 1) {
            if(ord.gea < 10) {
                diff += 1
                space = 4
            } else if (ord.gea >= 100) {
                space = 1
            }
        }else if(store.fontsize == 2) {
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