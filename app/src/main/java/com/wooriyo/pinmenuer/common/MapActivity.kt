package com.wooriyo.pinmenuer.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wooriyo.pinmenuer.BaseActivity
import com.wooriyo.pinmenuer.R
import com.wooriyo.pinmenuer.databinding.ActivityMapBinding
import com.wooriyo.pinmenuer.model.KakaoResultDTO
import com.wooriyo.pinmenuer.util.ApiClient
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapActivity : BaseActivity() {
    lateinit var binding: ActivityMapBinding

    val TAG = "MapActivity"

    private lateinit var mapView : MapView
    private lateinit var marker : MapPOIItem

    var lat : Double = 35.815982818603516               //우리요 위도
    var long: Double = 127.11023712158203             //우리요 경도
    var addr = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = MapView(this@MapActivity)
        mapView.setZoomLevel(1, true)

        binding.map.addView(mapView)

        marker = MapPOIItem()
        marker.tag = 0
        marker.itemName = ""
        marker.isShowCalloutBalloonOnTouch = false
        marker.markerType = MapPOIItem.MarkerType.BluePin

        addr = intent.getStringExtra("address").toString()
        val strLat = intent.getStringExtra("lat")
        val strLong = intent.getStringExtra("long")

        if(!strLat.isNullOrEmpty() && !strLong.isNullOrEmpty()) {
            lat = strLat.toDouble()
            long = strLong.toDouble()
        }
        setLocation()

        binding.back.setOnClickListener { finish() }
        binding.search.setOnClickListener { getCoordinate() }
        binding.save.setOnClickListener { save() }
    }

    // 좌표로 지도 중심, 마커 위치 설정
    private fun setLocation() {
        Log.d(TAG, "주소 $addr")
        Log.d(TAG, "위도 $lat")
        Log.d(TAG, "경도 $long")

        if(addr.isNotEmpty()) binding.etAddr.setText(addr)

        val point = MapPoint.mapPointWithGeoCoord(lat, long)
        marker.mapPoint = point

        mapView.setMapCenterPoint(point, false)
        mapView.addPOIItem(marker)
    }

    // 카카오 API > 주소 검색해서 위치 찾기
    private fun getCoordinate() {
        addr = binding.etAddr.text.toString()

        if(addr.isEmpty()) return

        ApiClient.kakaoService().kakaoSearch("KakaoAK ${getString(R.string.kakao_apiKey)}", addr)
            .enqueue(object : Callback<KakaoResultDTO>{
                override fun onResponse(call: Call<KakaoResultDTO>, response: Response<KakaoResultDTO>) {
                    Log.d(TAG, "주소 검색 URL : $response")
                    if(!response.isSuccessful) return

                    val result = response.body()
                    if(result == null)
                        return
                    else if(result.documents.isNotEmpty()) {
                        val addrDTO = result.documents[0]

                        addr = addrDTO.address_name
                        long = addrDTO.x.toDouble()
                        lat = addrDTO.y.toDouble()

                        setLocation()
                    }else
                        Toast.makeText(this@MapActivity, R.string.msg_no_search_result, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<KakaoResultDTO>, t: Throwable) {
                    Toast.makeText(this@MapActivity, R.string.msg_retry, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "주소 검색 실패 > $t")
                }
            })
    }

    fun save() {
        intent.putExtra("lat", lat.toString())
        intent.putExtra("long", long.toString())
        intent.putExtra("address", addr)
        setResult(RESULT_OK, intent)
        finish()
    }
}