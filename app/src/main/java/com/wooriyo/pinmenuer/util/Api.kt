package com.wooriyo.pinmenuer.util

import com.wooriyo.pinmenuer.model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface Api {
    // 버전 체크
    @GET("check_version.php")
    fun checkVersion(
        @Query("MODE") MODE: Int,           // 0 : 주문, 1 : 관리
        @Query("APPVS") APPVS: String,
        @Query("TYPE") type: Int        // 1: 모바일, 2: 태블릿
    ): Call<ResultDTO>

    @GET("checkmbr.php")
    fun checkMbr(
        @Query("userid") userid: String,
        @Query("pass") pass: String,
        @Query("push_token") push_token: String,
        @Query("os") os: String,
        @Query("osvs") osvs: Int,
        @Query("appvs") appvs: String,
        @Query("md") md: String,
        @Query("uuid") androidId : String,
        @Query("device_type") device_type: Int  // 0 : 모바일, 1 : 태블릿 (default 0)
    ): Call<MemberDTO>

    // 마스터 로그인
    @GET("masterLogin.php")
    fun masterLogin(
        @Query("userid") id:String,
        @Query("pass") pass:String
    ): Call<MemberDTO>

    //회원가입
//    @FormUrlEncoded
//    @POST("m/regmbr.php")
//    fun regMember(
//        @Field("userid") userid: String,
//        @Field("alpha_userid") arpayo_id: String,
//        @Field("user_pwd") pw: String,
//        @Field("push_token") push_token: String,
//        @Field("os") os: String,
//        @Field("osvs") osver: Int,
//        @Field("appvs") appver: String,
//        @Field("md") model: String
//    ): Call<MemberDTO>

    @GET("m/regmbr.php")
    fun regMember(
        @Query("userid") userid: String,
        @Query("alpha_userid") arpayo_id: String,
        @Query("user_pwd") pw: String,
        @Query("push_token") push_token: String,
        @Query("os") os: String,
        @Query("osvs") osver: Int,
        @Query("appvs") appver: String,
        @Query("md") model: String
    ): Call<MemberDTO>

    //아이디 중복 체크
    @FormUrlEncoded
    @POST("m/checkid.php")
    fun checkId(
        @Field("userid") userid: String
    ): Call<ResultDTO>

    //알파요 ID 연동
    @GET("m/checkalpha.php")
    fun checkArpayo(
        @Query("userid") arpayoId: String
    ): Call<ResultDTO>

    //회원정보 수정
    @GET("m/udtmbr.php")
    fun udtMbr (
        @Query("useridx") useridx : Int,
        @Query("pass") pass : String,
        @Query("emplyr_id") arpayo_id: String
    ): Call<ResultDTO>

    // 로그아웃
    @GET("m/logout.php")
    fun logout(
        @Query("useridx") useridx: Int,
        @Query("push_token") push_token: String
    ): Call<ResultDTO>

    //비밀번호 찾기
    @GET("u/find_pwd.php")
    fun findPwd (
        @Query("userid") userid: String
    ): Call<ResultDTO>

    // 탈퇴
    @GET("m/leave.php")
    fun dropMbr(
        @Query("useridx") useridx: Int
    ): Call<ResultDTO>

    // 매장 목록 조회
    @FormUrlEncoded
    @POST("m/store.list.php")
    fun getStoreList(
        @Field("useridx") useridx: Int,
        @Field("storeidx") storeidx: String?="" // null일 때 처리를 위해서 여기만 String
    ): Call<StoreListDTO>

    //매장 등록
    @GET("m/regstore.php")
    fun regStore(
        @Query("useridx") useridx: Int,
        @Query("storenm") storenm: String,
        @Query("addr") addr: String,               // 주소
        @Query("lclong") lclong: String,           // 매장 경도
        @Query("lclat") lclat: String              // 매장 위도
    ): Call<ResultDTO>

    //매장 정보 수정
    @GET("m/udtstore.php")
    fun udtStore(
        @Query("useridx") useridx: Int,
        @Query("idx") storeidx: Int,
        @Query("storenm") storenm: String,
        @Query("addr") addr: String,                // 주소
        @Query("lclong") lclong: String,            // 매장 경도
        @Query("lclat") lclat: String               // 매장 위도
    ): Call<ResultDTO>

    //매장 삭제
    @GET("m/delstore.php")
    fun delStore(
        @Query("useridx") useridx: Int,
        @Query("idx") storeidx: Int
    ): Call<ResultDTO>

    //매장 세부정보 설정
    @GET("m/udtstoredetail.php")
    fun udtStoreDetail (
        @Query("useridx") useridx: Int,
        @Query("idx") storeidx: Int,
        @Query("tel") tel : String,
        @Query("sns") sns : String,
        @Query("delivery") delivery : String,
        @Query("parking") parking : String,
        @Query("parkingadr") parkingAddr : String,
        @Query("p_long") p_long: String,                    // 주차장 경도
        @Query("p_lat") p_lat: String                           // 주차장 위도
    ): Call<ResultDTO>

    // 매장 대표 이미지 설정
    @Multipart
    @POST("m/uploadstore.php")
    fun udtStoreImg (
        @Part("useridx") useridx: Int,
        @Part("idx") storeidx: Int,
        @Part img: MultipartBody.Part?
    ): Call<ResultDTO>

    // 매장 영업시간 설정
    @FormUrlEncoded
    @POST("m/udtTimeDetail")
    fun udtStoreTime (
        @Field("useridx") useridx: Int,
        @Field("idx") storeidx: Int,
        @Field("JSONW") JSONW : String,
        @Field("JSONB") JSONB : String,
        @Field("JSONH") JSONH : String,
    ): Call<ResultDTO>

    // 매장 특별 휴일 저장
    @GET("m/ins_holiday.php")
    fun insHoliday (
        @Query("useridx") useridx: Int,
        @Query("idx") storeidx: Int,
        @Query("title") title : String,
        @Query("month") month : String,
        @Query("day") day : String
    ): Call<ResultDTO>

    // 매장 특별 휴일 수정
    @GET("m/udt_holiday.php")
    fun udtHoliday (
        @Query("useridx") useridx: Int,
        @Query("idx") holidayidx : Int,
        @Query("title") title : String,
        @Query("month") month : String,
        @Query("day") day : String
    ): Call<ResultDTO>

    // 매장 특별 휴일 삭제
    @GET("m/del_holiday.php")
    fun delHoliday (
        @Query("useridx") useridx: Int,
        @Query("idx") holidayidx : Int
    ): Call<ResultDTO>

    // 매장 이용자수 체크
    @GET("m/checkLimitedDevice.php")
    fun checkDeviceLimit(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String,
        @Query("token") token : String,
        @Query("device_type") device_type: Int  // 0 : 모바일, 1 : 태블릿 (default 0)
    ): Call<ResultDTO>

    // 매장 나갈 때 이용자수 차감
    @GET("m/leaveStore.php")
    fun leaveStore(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String
    ): Call<ResultDTO>

    // 카테고리 목록 조희
    @GET("m/getcategory.php")
    fun getCateList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
    ): Call<CateListDTO>

    // 카테고리 등록
    @GET("m/inscate.php")
    fun insCate(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("name") name : String,
        @Query("memo") subName : String,
        @Query("buse") buse : String
    ): Call<CateListDTO>

    // 카테고리 수정
    @GET("m/udtcate.php")
    fun udtCate(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("idx") cateidx: Int,
        @Query("name") name : String,
        @Query("memo") memo : String,
        @Query("buse") buse : String
    ): Call<ResultDTO>

    // 카테고리 삭제
    @GET("m/delcate.php")
    fun delCate(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("idx") cateidx: Int,
        @Query("code") code: String
    ): Call<ResultDTO>

    // 카테고리 순서 변경
    @GET("m/udtseq.php")
    fun udtCateSeq(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("JSON") JSON: String
    ): Call<CateListDTO>

    // 메뉴 조회
    @GET("m/goods.list.php")
    fun getGoods(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<GoodsListDTO>

    // 메뉴 등록
    @GET("m/ins_goods.php")
    fun insGoods(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("code") code: String,
        @Query("name") name : String,
        @Query("content") content : String,
        @Query("cooking_time_min") cooking_time_min : String,
        @Query("cooking_time_max") cooking_time_max : String,
        @Query("price") price : Int,
        @Query("adDisplay") adDisplay : String,
        @Query("icon") icon : Int,
        @Query("boption") boption : String,
        @Query("option_name") option_name: String,
        @Query("option_value") option_value: String,
        @Query("option_mark") option_mark: String,
        @Query("option_price") option_price: String,
        @Query("option_req") option_req: String
    ): Call<ResultDTO>

    // 메뉴 이미지 등록
    @Multipart
    @POST("upload_image.php")
    fun uploadImg(
        @Part("useridx") useridx: Int,
        @Part("gidx") gidx: Int,
        @Part img1: MultipartBody.Part?,
        @Part img2: MultipartBody.Part?,
        @Part img3: MultipartBody.Part?
    ): Call<ResultDTO>

    // 메뉴 수정
    @GET("m/udt_goods.php")
    fun udtGoods(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("gidx") gidx: Int,
        @Query("code") code: String,
        @Query("name") name : String,
        @Query("content") content : String,
        @Query("cooking_time_min") cooking_time_min : String,
        @Query("cooking_time_max") cooking_time_max : String,
        @Query("price") price : Int,
        @Query("icon") icon : Int,
        @Query("img1") img1: Int,       // 이미지 삭제 여부 1:삭제, 0: 유지
        @Query("img2") img2: Int,
        @Query("img3") img3: Int,
        @Query("adDisplay") adDisplay : String,
        @Query("boption") boption : String,
        @Query("option_code") option_code: String,
        @Query("option_name") option_name: String,
        @Query("option_value") option_value: String,
        @Query("option_mark") option_mark: String,
        @Query("option_price") option_price: String,
        @Query("option_req") option_req: String
    ): Call<ResultDTO>

    // 메뉴 삭제
    @GET("m/del_goods.php")
    fun delGoods(
        @Query("useridx") useridx: Int,
        @Query("idx") storeidx: Int,
        @Query("gidx") gidx: Int
    ): Call<ResultDTO>

    // 메뉴 순서 변경
    @GET("m/udt_goodseq.php")
    fun udtGoodSeq(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("JSON") JSON: String
    ): Call<ResultDTO>

    // 테이블 비밀번호 변경
    @GET("m/udt_tablepwd.php")
    fun udtTablePwd (
        @Query("useridx") useridx: Int,
        @Query("idx") storeidx: Int,
        @Query("pass") pwd: String
    ): Call<ResultDTO>

    // 메뉴판 테마색 설정
    @GET("m/setbg.php")
    fun setBgColor(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("thema_color") color: String // d : 어두운 배경(디폴트), g : 은색 배경, l : 밝은 배경
    ): Call<ResultDTO>

    // 메뉴판 뷰 모드 설정
    @GET("m/setviewmode.php")
    fun setViewMode(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("viewmode") viewmode: String // b : 기본, p: 3x3
    ): Call<ResultDTO>

    // 메뉴판 디자인(테마) 설정
    @GET("m/set_thema.php")
    fun setThema(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("thema_color") color: String, // d : 어두운 배경(디폴트), g : 은색 배경, l : 밝은 배경
        @Query("viewmode") viewmode: String // b : 기본, p: 3x3
    ): Call<ResultDTO>

    // 단건 주문 조회 (푸시)
    @GET("m/get.receipt.php")
    fun getReceipt(
        @Query("ordcode") ordcode: String,  // 주문 코드
    ): Call<ReceiptDTO>

    // 모든 테이블 내역 조회 (테이블별 조회)
    @GET("m/tableNo.AllOrdList.php")
    fun getTableHisList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<OrderListDTO>

    // 테이블별 전체 완료 처리
    @GET("m/udtCompletedAllOrder.php")
    fun setTableComplete(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("tableNo") tableNo: String,
        @Query("iscompleted") iscompleted: String
    ):Call<ResultDTO>

    // 테이블별 전체 내역 조회 (테이블별 조회)
    @GET("m/tableNo.TotalList.php")
    fun getTableTotalList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("tableNo") tableNo: String
    ): Call<OrderListDTO>

    // 테이블별 주문 리스트
    @GET("m/tableNo.OrdList.php")
    fun getTableOrderList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("tableNo") tableNo: String
    ): Call<OrderListDTO>

    // 테이블별 호출 목록 조회
    @GET("m/tableNo.CallList.php")
    fun getTableCallList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("tableNo") tableNo: String
    ): Call<CallListDTO>

    // 테이블별 완료 리스트
    @GET("m/tableNo.CompletedList.php")
    fun getTableCompletedList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("tableNo") tableNo: String
    ): Call<OrderListDTO>

    // 테이블별 포스 전송실패 리스트
    @GET("m/tableNo.PosList.php")
    fun getTablePosErrList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("tableNo") tableNo: String
    ): Call<OrderListDTO>

    // 전체 목록(히스토리)(주문,호출) 조회
    @GET("m/allorder.list.php")
    fun getTotalList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<OrderListDTO>

    // 완료 목록 조회
    @GET("m/orderCompleted.list.php")
    fun getCompletedList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<OrderListDTO>

    // 포스 전송 실패 목록 조회
    @GET("m/posorder.list.php")
    fun getPosErrorList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<OrderListDTO>

    // 포스 재전송
    @GET("m/pos_send.php")
    fun sendToPos(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("gidx") idx: Int,            // 주문 idx
        @Query("tableNo") tableNo: String
    ): Call<OrderListDTO>

    // 주문만 호출
    @GET("m/isorder.list.php")
    fun getOrderList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<OrderListDTO>

    // 주문 완료
    @GET("m/udtCompletedOrder.php")
    fun udtComplete(
        @Query("storeidx") storeidx: Int,
        @Query("ordidx") ordidx: Int,
        @Query("iscompleted") iscompleted: String
//        @Query("popup") popup: Int                   // 1: 핍업 다시보지않기 클릭, 0 : 팝업 다시보지않기 미클릭
    ): Call<ResultDTO>

    // 주문 결제 완료
    @GET("m/payCardReader.php")
    fun insPayCard(
        @Query("storeidx") storeidx: Int,
        @Query("JSON") JSON: String
    ): Call<ResultDTO>

    // 주문 삭제
    @GET("m/delete_order.php")
    fun deleteOrder(
        @Query("storeidx") storeidx: Int,
        @Query("ordidx") ordidx: Int
    ): Call<ResultDTO>

    // 주문 목록 초기화
    @GET("m/del_order.php")
    fun clearOrder(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<ResultDTO>

    // 직원 호출 히스토리 조회
    @GET("m/callHistory.php")
    fun getCallHistory(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<CallListDTO>

    // 직원 호출 완료 처리
    @GET("m/udtCompletedCall.php")
    fun completeCall(
        @Query("storeidx") storeidx: Int,
        @Query("ordidx") ordidx: Int,
        @Query("iscompleted") iscompleted: String
    ): Call<ResultDTO>

    // 직원 호출 목록 초기화
    @GET("m/del_callHistory.php")
    fun clearCall(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<ResultDTO>


    // 직원 호출 등록된 목록 조회 >> (고객이 호출한 내역 아님!! 관리자가 등록했던 리스트 전체)
    @GET("m/call.list.php")
    fun getCallList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<CallSetListDTO>

    // 직원 호출 등록
    @GET("m/ins_call.php")
    fun insCall(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("name") callName : String
    ): Call<ResultDTO>

    // 직원 호출 수정
    @GET("m/udt_call")
    fun udtCall (
        @Query("useridx") useridx: Int,
        @Query("idx") callidx: Int,
        @Query("name") callName : String
    ): Call<ResultDTO>

    // 직원 호출 삭제
    @GET("m/del_call.php")
    fun delCall(
        @Query("useridx") useridx: Int,
        @Query("idx") callidx: Int
    ): Call<ResultDTO>

    // 영수증 프린터 관련 Api
    // 프린터 설정 최조 진입 시
    @GET("m/ins_print_setting.php")
    fun insPrintSetting(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String
    ): Call<ResultDTO>

    // 등록한 프린터 목록
    @GET("m/connect_print_list.php")
    fun connPrintList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String
    ): Call<PrintListDTO>

    // 프린터 별명 설정
    @GET("m/print_nick.php")
    fun setPrintNick(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String,
        @Query("nick") nick: String,
        @Query("type") type: Int        // 1 : 관리자 기기, 2 : 프린트 기기
    ): Call<ResultDTO>

    // 사용가능한 프린터 목록
    @GET("m/print_model_list.php")
    fun getSupportList(): Call<PrintModelListDTO>

    // 프린터 삭제
    @GET("m/del_print.php")
    fun delPrint(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String,
        @Query("idx") idx: Int
    ): Call<ResultDTO>

    // 프린터 기기 선택
    @GET("m/udt_print_kind.php")
    fun udtPrintModel(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String,
        @Query("printType") printType: Int,
        @Query("blstatus") blstatus : String
    ): Call<ResultDTO>

    // 프린터 출력 설정 불러오기
    @GET("m/getprintinfo.php")
    fun getPrintContentSet(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String
    ): Call<PrintContentDTO>

    // 프린터 출력 설정 하기
    @GET("m/udt_print_setting.php")
    fun setPrintContent(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String,
        @Query("idx") idx: Int,
        @Query("fontSize") fontSize: Int,   // 1: 크게 , 2 : 작게
        @Query("kitchen") kitchen: String,  // 주방영수증 사용 여부 (Y: 사용, N: 미사용)
        @Query("receipt") receipt: String,  // 고객영수증 사용 여부 (Y: 사용, N: 미사용)
        @Query("ordcode") ordcode: String,  // 주문번호 사용 여부 (Y: 사용, N: 미사용)
        @Query("cate") category: String
    ): Call<PrintContentDTO>

    // 프린터 연결 상태값 저장
    @GET("m/udt_print_connect_status.php")
    fun setPrintConnStatus (
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String,
        @Query("idx") idx: Int,
        @Query("blstatus") blstatus: String
    ): Call<ResultDTO>


    // 결제 설정 관련 Api
    // 결제 설정 최초 진입 시
    @GET("m/ins_pay_setting.php")
    fun insPaySetting(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String
    ): Call<ResultDTO>

    // 결제 설정하기
    @GET("m/udt_pay_setting.php")
    fun udtPaySettting(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String,
        @Query("idx") idx: Int,             // sw_paysetting 테이블 idx
        @Query("qrbuse") qrbuse: String,     // QR 사용 여부 (Y: 사용 , N :미사용 )
        @Query("cardbuse") cardbuse: String // 카드결제 사용 여부 (Y: 사용 , N :미사용 )
    ): Call<ResultDTO>

    // 결제 설정 불러오기
    @GET("m/getpayinfo.php")
    fun getPayInfo(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String
    ): Call<PaySettingDTO>

    //나이스페이먼츠 key 설정
    @GET("m/ins_mid_setting.php")
    fun insMidSetting(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("uuid") androidId : String,
        @Query("idx") idx: Int,             // sw_paysetting 테이블 idx
        @Query("mid") mid : String,         // mid (아이디)
        @Query("sc_key") sc_key : String    // 키 값
    ): Call<ResultDTO>

    //PG 심사용 사업자등록증 정보 입력
    @GET("m/ins_pginfo.php")
    fun insPgInfo(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("snum") snum: String,
        @Query("storenm") storeName: String,
        @Query("ceo") ceo: String,
        @Query("tel") tel: String,
        @Query("addr") addr: String,
    ): Call<ResultDTO>

    // pg 결제 고객 정보 받기 설정
    @GET("m/set_qrcustominfo.php")
    fun setQrCustomInfo(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("blnm") name: String,
        @Query("blph") phone: String,
        @Query("bladr") addr: String,
        @Query("bletc") etc: String,
        @Query("blmemo") memo: String,
        @Query("memo") strMemo: String
    ): Call<ResultDTO>

    // pg 결제 고객 정보 받기 - 테이블 리스트
    @GET("m/custom.tablelist.php")
    fun getTableList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<PgResultDTO>

    // pg 결제 내역
    @GET("m/pg.list.php")
    fun getPgList(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int
    ): Call<PgResultDTO>

    // pg 결제 내역 상세
    @GET("m/pgDetail.php")
    fun getPgDetail(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("ordcode") ordcode: String
    ): Call<PgDetailResultDTO>

    // pg 결제 취소
    @GET("m/pg.cancel.php")
    fun cancelPG(
        @Query("useridx") useridx: Int,
        @Query("storeidx") storeidx: Int,
        @Query("tid") tid: String
    ): Call<ResultDTO>

    // 카카오 지도 관련 api
    @GET("/v2/local/search/address.json")
    fun kakaoSearch(
        @Header("Authorization") key : String,
        @Query("query") query : String
    ): Call<KakaoResultDTO>
}