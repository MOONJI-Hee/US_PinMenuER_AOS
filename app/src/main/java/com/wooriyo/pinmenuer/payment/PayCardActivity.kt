package com.wooriyo.pinmenuer.payment

import android.content.ClipData
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.broadcast.EasyCheckReceiver
import com.wooriyo.pinmenuer.databinding.ActivityPayCardBinding
import com.wooriyo.pinmenuer.listener.EasyCheckListener
import com.wooriyo.pinmenuer.listener.ItemClickListener
import com.wooriyo.pinmenuer.model.OrderDTO
import com.wooriyo.pinmenuer.model.OrderHistoryDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.payment.adapter.OrderDetailAdapter
import com.wooriyo.pinmenuer.util.ApiClient
import com.wooriyo.pinmenuer.util.AppHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PayCardActivity : AppCompatActivity() {
    val TAG = "PayCardActivity"
    val mActivity = this@PayCardActivity

    lateinit var binding: ActivityPayCardBinding
    lateinit var order : OrderHistoryDTO
    lateinit var olist : ArrayList<OrderDTO>
    lateinit var adapter: OrderDetailAdapter

    var totGea = 0
    var totPrice = 0
    var charge = 0
    var remain = 0

    var checkedAll = true

    lateinit var receiver : EasyCheckReceiver

    val goKICC = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            Log.d(TAG, "결제 성공")
            //직전거래에 대한 취소거래필요정보를 받음
            val cancelInfo: Intent = it.data ?: return@registerForActivityResult

            val rtn = it.data
            if(rtn != null) {
                val data = rtn.data
                Log.d(TAG, "return 값 >> $data")

                insPayCard(data.toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        order = intent.getSerializableExtra("order") as OrderHistoryDTO
        olist = order.olist
        olist.forEach { it.isChecked = true }
        adapter = OrderDetailAdapter(olist)

        adapter.setOnCheckListener(object : ItemClickListener{
            override fun onCheckClick(position: Int, v: CheckBox, isChecked: Boolean) {
                val oPrice = olist[position].price * olist[position].gea

                if(isChecked) {
                    charge += oPrice
                }else {
                    charge -= oPrice
                }

                olist[position].isChecked = isChecked
                remain = totPrice - charge

                binding.chargePrice.text = AppHelper.price(charge)
                binding.remainPrice.text = AppHelper.price(remain)

                Log.d(TAG, "totGea >> $totGea")
                Log.d(TAG, "totPrice >> $totPrice")
                Log.d(TAG, "chargePrice >> $charge")
                Log.d(TAG, "remainPrice >> $remain")


                Log.d(TAG, "oPrice >> $oPrice")

                olist.forEach {
                    if(!it.isChecked) {
                        binding.checkAll.isChecked = false
                        return
                    }
                }
                binding.checkAll.isChecked = true
            }
        })

        binding.rv.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding.rv.adapter = adapter

        totGea = order.olist.size
        totPrice = order.amount
        charge = totPrice

        binding.totGea.text = String.format(getString(R.string.total_gea), totGea)
        binding.totPrice.text = AppHelper.price(totPrice)
        binding.chargePrice.text = AppHelper.price(charge)

        Log.d(TAG, "totGea >> $totGea")
        Log.d(TAG, "totPrice >> $totPrice")
        Log.d(TAG, "chargePrice >> $charge")
        Log.d(TAG, "remainPrice >> $remain")

        binding.tableNo.text = order.tableNo
        binding.regdt.text = order.regdt

        binding.back.setOnClickListener { finish() }
        binding.payment.setOnClickListener { payOrder() }
        binding.done.setOnClickListener {
            setResult(RESULT_OK, intent)
            finish()
        }
        binding.dutch.setOnClickListener {
            Toast.makeText(mActivity, R.string.msg_prepare, Toast.LENGTH_SHORT).show()
        }

        binding.checkAll.setOnClickListener {
            it as CheckBox
            if(it.isChecked) {
                checkedAll = true
                for (orderDTO in order.olist) {
                    orderDTO.isChecked = true
                }
            }else {
                checkedAll = false
                for (orderDTO in order.olist) {
                    orderDTO.isChecked = false
                }
            }
            adapter.notifyDataSetChanged()
        }

        receiver = EasyCheckReceiver()
        receiver.setOnEasyCheckListener(object : EasyCheckListener {
            override fun getIntent(intent: Intent?) {
                //로그확인
                Log.e("heykyul", "broadcast 들어옴")
            }
        })
        val filter = IntentFilter("kr.co.kicc.ectablet.broadcast")
        this.registerReceiver(receiver, filter)
    }


    // 카드 결제 처리 (KICC 앱으로 이동)
    fun payOrder() {
        val compName = ComponentName("kr.co.kicc.ectablet", "kr.co.kicc.ectablet.SmartCcmsMain")

        val intent = Intent(Intent.ACTION_MAIN)

        intent.putExtra("APPCALL_TRAN_NO", AppHelper.getAppCallNo())
        intent.putExtra("TRAN_TYPE", "credit")
        intent.putExtra("TOTAL_AMOUNT", charge.toString())

        val tax = (charge * 0.1).toInt()
        intent.putExtra("TAX", tax.toString())
        intent.putExtra("TIP", "0")
        intent.putExtra("INSTALLMENT", "0")
        intent.putExtra("UI_SKIP_OPTION", "NNNNN")

        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.component = compName

        try {
            goKICC.launch(intent)
        }catch (e: Exception) {
            Toast.makeText(mActivity, R.string.msg_no_card_reader, Toast.LENGTH_SHORT).show()
        }
    }

    // 카드 결제 후 결과 저장
    fun insPayCard(data: String) {
        ApiClient.service.insPayCard(MyApplication.storeidx, data).enqueue(object :
            Callback<ResultDTO> {
            override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                Log.d(TAG, "카드 결제 결과 저장 url : $response")
                if(!response.isSuccessful) return

                val result = response.body() ?: return
                when(result.status){
                    1 -> {
//                        Toast.makeText(mActivity, R.string.complete, Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "카드 결제 결과 저장 실패 > $t")
                Log.d(TAG, "카드 결제 결과 저장 실패 > ${call.request()}")
            }
        })
    }
}