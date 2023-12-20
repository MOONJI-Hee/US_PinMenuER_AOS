package com.wooriyo.pinmenuer.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.MyApplication
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.common.InfoDialog
import com.wooriyo.pinmenuer.databinding.ActivitySetPayBinding
import com.wooriyo.pinmenuer.model.PaySettingDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetPayActivity : BaseActivity() {
    lateinit var binding: ActivitySetPayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPayInfo()

        binding.back.setOnClickListener { finish() }

        binding.usableDevice.setOnClickListener { startActivity(Intent(mActivity, ReaderModelActivity::class.java)) }

        binding.infoQR.setOnClickListener{
            InfoDialog(mActivity, getString(R.string.use_post_QR), getString(R.string.use_post_QR_info)).show()
        }
        binding.infoCard.setOnClickListener{
            InfoDialog(mActivity, getString(R.string.use_post_card), getString(R.string.use_post_card_info)).show()
        }
    }

    fun setView(settingDTO: PaySettingDTO) {
        binding.ckPostQR.isChecked = settingDTO.qrbuse == "Y"
        binding.ckPostCard.isChecked = settingDTO.cardbuse == "Y"

        val stts = if(settingDTO.mid.isNotEmpty() && settingDTO.mid_key.isNotEmpty()) "연결완료" else "연결전"
        binding.statusQR.text = String.format(getString(R.string.payment_status), stts)

        // 처음 진입했을 때는 이벤트 발생하지 않도록 위치 조정
        binding.ckPostQR.setOnCheckedChangeListener { _, _ -> udtPaySetting() }
        binding.ckPostCard.setOnCheckedChangeListener { _, _ -> udtPaySetting() }

        // key 유무에 따라 클릭 이벤트 변화
        binding.setQR.setOnClickListener {
            if(settingDTO.mid.isEmpty() || settingDTO.mid_key.isEmpty()) {
                startActivity(Intent(mActivity, NicepayInfoActivity::class.java))
            }else{
                val intent = Intent(mActivity, SetPgInfoActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun getPayInfo() {
        ApiClient.service.getPayInfo(MyApplication.useridx, MyApplication.storeidx, MyApplication.androidId)
            .enqueue(object: Callback<PaySettingDTO> {
                override fun onResponse(call: Call<PaySettingDTO>, response: Response<PaySettingDTO>) {
                    Log.d(TAG, "결제 설정 조회 URL : $response")
                    if(!response.isSuccessful) return

                    val result = response.body() ?: return
                    when(result.status) {
                        1 -> setView(result)
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PaySettingDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "결제 설정 조회 오류 >> $t")
                    Log.d(TAG, "결제 설정 내용 조회 오류 >> ${call.request()}")
                }
            })
    }

    fun udtPaySetting() {
        val checkQr = if(binding.ckPostQR.isChecked) "Y" else "N"
        val checkCard = if(binding.ckPostCard.isChecked) "Y" else "N"

        ApiClient.service.udtPaySettting(MyApplication.useridx, MyApplication.storeidx, MyApplication.androidId, MyApplication.bidx, checkQr, checkCard)
            .enqueue(object : Callback<ResultDTO>{
                override fun onResponse(call: Call<ResultDTO>, response: Response<ResultDTO>) {
                    Log.d(TAG, "결제 설정 URL : $response")
                    if(!response.isSuccessful) return
                    val result = response.body() ?: return

                    when(result.status) {
                        1 -> Toast.makeText(mActivity, R.string.msg_complete, Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(mActivity, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResultDTO>, t: Throwable) {
                    Toast.makeText(mActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "결제 설정 오류 >> $t")
                    Log.d(TAG, "결제 설정 오류 >> ${call.request()}")
                }
            })
    }
}