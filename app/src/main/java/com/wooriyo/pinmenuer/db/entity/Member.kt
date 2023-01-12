package com.wooriyo.pinmenuer.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Member (
    @PrimaryKey var idx: Int,
    @ColumnInfo var userid: String,
    @ColumnInfo var storenm: String?,
    @ColumnInfo var userlv: Int,
    @ColumnInfo var pass: String,
    @ColumnInfo var name: String?,
    @ColumnInfo var bsms: String? = "Y",
    @ColumnInfo var memo: String?,
    @ColumnInfo var ip: String?,
    @ColumnInfo var logdt: String?,
    @ColumnInfo var vcnt: Int,
    @ColumnInfo var status: Int?,
    @ColumnInfo var regdt: String?,
    @ColumnInfo var updt: String?,
    @ColumnInfo var osver: String,
    @ColumnInfo var os: String,
    @ColumnInfo var appver: String,
    @ColumnInfo var model: String,
    @ColumnInfo var push_token: String,
    @ColumnInfo var emplyr_id: String?,
    @ColumnInfo var payuse: String? = "N",
    @ColumnInfo var paydt: String?,
    @ColumnInfo var startdt: String?,
    @ColumnInfo var enddt: String?,
    @ColumnInfo var admtbl: Int
)