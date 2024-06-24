package com.wooriyo.us.pinmenuer.db.dao

import androidx.room.*
import com.wooriyo.us.pinmenuer.db.entity.Category

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