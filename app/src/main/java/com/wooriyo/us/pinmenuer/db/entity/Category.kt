package com.wooriyo.us.pinmenuer.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey val idx: Int,
    @ColumnInfo val pidx: Int?,
    @ColumnInfo val code: String,
    @ColumnInfo val name: String,
    @ColumnInfo val memo: String?,
    @ColumnInfo val buse: String? = "Y",
    @ColumnInfo val bgnb: String? = "Y",
    @ColumnInfo val seq: Int? = 0,
    @ColumnInfo val regdt: String?,
    @ColumnInfo val updt: String?
)