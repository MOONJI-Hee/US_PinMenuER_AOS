package com.wooriyo.us.pinmenuer.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OrderHistoryDTO(
    @SerializedName("status") var status: Int,
    @SerializedName("msg") var msg: String,
    @SerializedName("ordidx") var idx : Int,                            // 주문 idx
    @SerializedName("storeidx") var storeidx : Int,                     // 매장 idx
    @SerializedName("tableNo") var tableNo : String,                    // 테이블 번호
    @SerializedName("ordcode") var ordcode : String,                    // 주문 번호 (매일 갱신)
    @SerializedName("ordcode_key") var ordcode_key : String,            // 주문 코드
    @SerializedName("paytype") var paytype : Int,                       // 3: 큐알 결제 완료, 0: 그 외
    @SerializedName("ordType") var ordType: Int,                        // 1: 주문, 2: 호출
    @SerializedName("reserType") var reserType: Int,                    // 0: 일반 주문, 1: 예약, 2: 포장
    @SerializedName("isreser") var isreser: Int,                        // 0: 예약 확인 전, 1: 예약 확인 완료
    @SerializedName("glist") var olist : ArrayList<OrderDTO>,           // 주문 상세 리스트
    @SerializedName("rlist") var rlist : ArrayList<ReservationDTO>,     // 예약 정보
    @SerializedName("amount") var amount : Double,                      // 상품 총 금액
    @SerializedName("tip")      var tip : Double,                       // 팁 총 금액
    @SerializedName("tipPer")   var tipPer : Int,                       // 팁 %
    @SerializedName("tax")      var tax : Double,                       // 택스 총 금액
    @SerializedName("taxPer")   var taxPer : Double,                    // 택스 %
    @SerializedName("regdt") var regdt : String,                        // 등록일시 (주문일시)
    @SerializedName("iscompleted") var iscompleted : Int,               // 완료 여부 (1 : 완료, 0 : 미완료)
    @SerializedName("complate_updt") var complate_updt : String,        // 완료일시 (결제일시)

    @SerializedName("total_gea") var total : Int,                       // 총 개수
    @SerializedName("total_price") var total_price : Double,            // 상품 + 팁 + 택스 총 금액

    @SerializedName("TotalOrdCnt") var totalOrdCnt: Int,                // 테이블별 주문보기 - 주문 개수
    @SerializedName("TotalCallCnt") var totalCallCnt: Int,              // 테이블별 주문보기 - 호출 개수

    @SerializedName("reserStoreCnt") var reservStoreCnt: Int,           // 테이블별 주문보기 - 주문 개수
    @SerializedName("reserTakeCnt") var reservTogoCnt: Int,             // 테이블별 주문보기 - 호출 개수

    @SerializedName("completed_total_price") var completedPrice: Double,// 테이블별 주문보기 - 완료된 가격
    @SerializedName("completed_total_gea") var completedGea: Int        // 테이블별 주문보기 - 완료된 개수
): Serializable