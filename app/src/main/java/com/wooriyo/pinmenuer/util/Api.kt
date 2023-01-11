package com.wooriyo.pinmenuer.util

import com.wooriyo.pinmenuer.model.MemberDTO
import com.wooriyo.pinmenuer.model.ResultDTO
import retrofit2.Call
import retrofit2.http.*

interface Api {

    @GET("/api/checkmbr.php")
    fun checkmbr(
        @Query("userid") userid: String,
        @Query("pass") pass: String,
        @Query("push_token") push_token: String,
        @Query("os") os: String,
        @Query("osvs") osvs: String,
        @Query("appvs") appvs: String,
        @Query("md") md: String
    ): Call<MemberDTO>

    //아이디 중복 체크
    @FormUrlEncoded
    @POST("/api/m/checkid.php")
    fun checkId(
        @Field("userid") userid: String
    ): Call<ResultDTO>

    //알파요 ID 연동
    @FormUrlEncoded
    @POST("/api/m/checkalpha.php")
    fun checkArpayo(
        @Field("userid") userid: String
    ): Call<ResultDTO>

    @FormUrlEncoded
    @POST("/api/m/regmbr.php")
    fun regMember(
        @Field("userid") userid: String,
        @Field("alpha_userid") arpayo_id: String,
        @Field("user_pwd") pw: String,
        @Field("push_token") push_token: String,
        @Field("os") os: String,
        @Field("osvs") osvs: String,
        @Field("appvs") appvs: String,
        @Field("md") md: String
    ): Call<MemberDTO>

//    //매장등록
//    @GET("/api/m/regstore.php")
//    fun regstore(
//        @Query("useridx") useridx: String,
//        @Query("storenm") storenm: String,
//        @Query("zip") zip: String,
//        @Query("addr") addr: String
//    ): Call<StoreListDTO>
//
//    //매장 불러오기
//    @FormUrlEncoded
//    @POST("/api/m/store.list.php")
//    fun storelist(
//        @Field("useridx") useridx: String
//    ): Call<StoreListDTO>
//
//    //매장 업데이트
//    @FormUrlEncoded
//    @POST("/api/m/uptstore.php")
//    fun uptstore(
//        @Field("idx") idx: String,
//        @Field("useridx") useridx: String,
//        @Field("storenm") storenm: String,
//        @Field("addr") addr: String,
//        @Field("zip") zip: String
//    ): Call<StoreListDTO>
//
//    //매장 삭제
//    @FormUrlEncoded
//    @POST("/api/m/delstore.php")
//    fun delstore(
//        @Field("useridx") useridx: String,
//        @Field("idx") idx: String
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