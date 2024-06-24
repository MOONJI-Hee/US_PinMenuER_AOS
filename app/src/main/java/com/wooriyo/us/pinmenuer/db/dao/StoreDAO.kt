package com.wooriyo.us.pinmenuer.db.dao

import androidx.room.*
import com.wooriyo.us.pinmenuer.db.entity.Store

@Dao
interface StoreDAO {
    @Insert
    fun insert(store: Store)

    @Update
    fun update(store: Store)

    @Delete
    fun delete(store: Store)

    @Query("SELECT * FROM Store WHERE pidx = :pidx")
    fun selAll(pidx:Int) : List<Store>
}