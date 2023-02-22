package com.wooriyo.pinmenuer.util

import com.wooriyo.pinmenuer.model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @FormUrlEncoded
    @POST("checkmbr.php")
    fun checkMbr(
        @Field("userid") userid: String,
        @Field("pass") pass: String,
        @Field("push_token") push_token: String,
        @Field("os") os: String,
        @Field("osvs") osvs: Int,
        @Field("appvs") appvs: String,
        @Field("md") md: String
    ): Call<MemberDTO>

    //회원가입
    @FormUrlEncoded
    @POST("m/regmbr.php")
    fun regMember(
        @Field("userid") userid: String,
        @Field("alpha_userid") arpayo_id: String,
        @Field("user_pwd") pw: String,
        @Field("push_token") push_token: String,
        @Field("os") os: String,
        @Field("osvs") osver: Int,
        @Field("appvs") appver: String,
        @Field("md") model: String
    ): Call<MemberDTO>

    //아이디 중복 체크
    @FormUrlEncoded
    @POST("m/checkid.php")
    fun checkId(
        @Field("userid") userid: String
    ): Call<ResultDTO>

    //알파요 ID 연동
    @FormUrlEncoded
    @POST("m/checkalpha.php")
    fun checkArpayo(
        @Field("userid") arpayoId: String
    ): Call<ResultDTO>

    //회원정보 수정
    @GET("m/udtmbr.php")
    fun udtMbr (
        @Query("useridx") useridx : Int,
        @Query("pass") pass : String,
        @Query("alpha_userid") arpayo_id: String
    ): Call<ResultDTO>

    // 매장 목록 조회
    @FormUrlEncoded
    @POST("/api/m/store.list.php")
    fun getStoreList(
        @Field("useridx") useridx: Int,
        @Field("storeidx") storeidx: String?="" // null일 때 처리를 위해서 여기만 String
    ): Call<StoreListDTO>

    //매장 등록
    @GET("m/regstore.php")
    fun regStore(
        @Query("useridx") useridx: Int,
        @Query("storenm") storenm: String,
        @Query("addr") addr: String,                // 주소
        @Query("lclong") lclong: String,           // 매장 경도
        @Query("lclat") lclat: String                  // 매장 위도
    ): Call<ResultDTO>

    //매장 정보 수정
    @GET("m/udtstore.php")
    fun udtStore(
        @Query("useridx") useridx: Int,
        @Query("idx") storeidx: Int,
        @Query("storenm") storenm: String,
        @Query("addr") addr: String,                // 주소
        @Query("lclong") lclong: String,           // 매장 경도
        @Query("lclat") lclat: String                  // 매장 위도
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
        @Part img: MultipartBody.Part?,
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
//    @FormUrlEncoded
//    @POST("m/ins_holiday.php")
//    fun insHoliday (
//        @Field("useridx") useridx: Int,
//        @Field("idx") storeidx: Int,
//        @Field("title") title : String,
//        @Field("month") month : String,
//        @Field("day") day : String
//    ): Call<ResultDTO>

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

    // 카테고리 목록 조희
    @GET("/api/m/getcategory.php")
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
    ): Call<ResultDTO>

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
        @Query("idx") cateidx: Int,
        @Query("code") code: String,
        @Query("seq") seq: Int
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
        @Query("thema_color") color: String // d : 어두운 배경, g : 은색 배경, l : 밝은 배경, 디폴트
    ): Call<ResultDTO>

    // 메뉴판 뷰 모드 설정
    @GET("m/setviewmode.php")
    fun setViewMode(
        @Query("useridx") useridx: Int,
        @Query("viewmode") viewmode: String // b : 기본, p: 3x3
    ): Call<ResultDTO>

    // 직원 호출 목록 조회
    @GET("m/call.list.php")
    fun getCallList(
        @Query("useridx") useridx: Int
    ): Call<CallSetListDTO>

    // 직원 호출 등록
    @GET("m/ins_call.php")
    fun insCall(
        @Query("useridx") useridx: Int,
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
}