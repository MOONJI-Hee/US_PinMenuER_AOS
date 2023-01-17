package com.wooriyo.pinmenuer.util

import com.wooriyo.pinmenuer.model.MemberDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @GET("checkmbr.php")
    fun checkMbr(
        @Query("userid") userid: String,
        @Query("pass") pass: String,
        @Query("push_token") push_token: String,
        @Query("os") os: String,
        @Query("osvs") osvs: Int,
        @Query("appvs") appvs: String,
        @Query("md") md: String
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
        @Field("userid") userid: String
    ): Call<ResultDTO>

    //회원정보 수정
    @GET("m/udtmbr.php")
    fun udtMbr (
        @Query("useridx") useridx : Int,
        @Query("pass") pass : String,
        @Query("alpha_userid") arpayo_id: String
    ): Call<ResultDTO>

    //매장 등록
    @GET("m/regstore.php")
    fun regStore(
        @Query("useridx") useridx: Int,
        @Query("storenm") storenm: String,
        @Query("addr") addr: String,                // 주소
        @Query("zip") zip: String                     // 우편번호
    ): Call<ResultDTO>

    //매장 정보 수정
    @GET("m/udtstore.php")
    fun udtStore(
        @Query("useridx") useridx: Int,
        @Query("idx") storeidx: Int,
        @Query("storenm") storenm: String,
        @Query("addr") addr: String,                // 주소
        @Query("zip") zip: String                     // 우편번호
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
        @Query("parkingadr") parkingAddr : String
    ): Call<ResultDTO>

//    //매장 불러오기
//    @FormUrlEncoded
//    @POST("/api/m/store.list.php")
//    fun storelist(
//        @Field("useridx") useridx: String
//    ): Call<StoreListDTO>


//
//    //카테고리 리스트 불러오기
//    @GET("/api/m/cate.list.php")
//    fun cateList(
//        @Query("useridx") useridx: String
//    ): Call<CategoryListDTO>
//
//    //카테고리 등록
//    @FormUrlEncoded
//    @POST("/api/m/inscate.php")
//    fun insCate(
//        @Field("useridx") useridx: String,
//        @Field("name") name: String,
//        @Field("memo") memo: String,
//        @Field("buse") buse: String
//    ): Call<CategoryListDTO>
//
//    //카테고리 수정
//    @FormUrlEncoded
//    @POST("/api/m/udtcate.php")
//    fun udtCate(
//        @Field("useridx") useridx: String,
//        @Field("idx") idx: String,
//        @Field("name") name: String,
//        @Field("memo") memo: String,
//        @Field("buse") buse: String
//    ): Call<CategoryListDTO>
//
//    //카테고리 삭제
//    @FormUrlEncoded
//    @POST("/api/m/delcate.php")
//    fun delCate(
//        @Field("useridx") useridx: String,
//        @Field("code") code: String,
//    ): Call<CategoryListDTO>
//
//    //배경 선택
//    @FormUrlEncoded
//    @POST("/api/m/setbg.php")
//    fun setbg(
//        @Field("useridx") useridx: String,
//        @Field("thema_color") thema_color: String
//    ): Call<CategoryListDTO>
//
//    //뷰어 모드 선택
//    @FormUrlEncoded
//    @POST("/api/m/setviewmode.php")
//    fun setViewMode(
//        @Field("useridx") useridx: String,
//        @Field("viewmode") viewmode: String
//    ): Call<CategoryListDTO>


}