package com.wooriyo.pinmenuer.db.dao

import androidx.room.*
import com.wooriyo.pinmenuer.db.entity.Category

@Dao
interface CategoryDAO {
    @Insert
    fun insert(category: Category)

    @Update
    fun update(category: Category)

    @Delete
    fun delete(category: Category)

    @Query("SELECT * FROM Category WHERE pidx = :pidx")
    fun selAll(pidx:Int) : List<Category>
}