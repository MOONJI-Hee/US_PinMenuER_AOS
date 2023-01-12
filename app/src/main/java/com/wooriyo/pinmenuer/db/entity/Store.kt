package com.wooriyo.pinmenuer.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Store(
    @PrimaryKey val idx : Int,
    @ColumnInfo val pidx: Int ?= 0,
    @ColumnInfo val buse: String,
    @ColumnInfo val admtbl: Int,
    @ColumnInfo val name: String?,
    @ColumnInfo val zip: String?,
    @ColumnInfo val adr1: String?,
    @ColumnInfo val adr2: String?,
    @ColumnInfo val tel: String?,
    @ColumnInfo val sns: String,
    @ColumnInfo val delivery: String?="N",
    @ColumnInfo val parking: String?="N",
    @ColumnInfo val parkingadr: String?,
    @ColumnInfo val img: String?,   // 추후에 비트맵으로 바꾸기
    @ColumnInfo val payuse: String,
    @ColumnInfo val paydate: String?,
    @ColumnInfo val regdt: String,
    @ColumnInfo val updt: String?,
    @ColumnInfo val status: String?="N",
    @ColumnInfo val statusdt: String?,
    @ColumnInfo val bkuse: String?= "N", // 브레이크타임 사용 유무
    @ColumnInfo val mon_use: String?= "N",
    @ColumnInfo val tue_use: String?= "N",
    @ColumnInfo val wed_use: String?= "N",
    @ColumnInfo val thu_use: String?= "N",
    @ColumnInfo val fri_use: String?= "N",
    @ColumnInfo val sat_use: String?= "N",
    @ColumnInfo val sun_use: String?= "N"
)