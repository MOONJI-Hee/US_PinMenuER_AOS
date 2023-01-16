package com.wooriyo.pinmenuer.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wooriyo.pinmenuer.db.dao.CategoryDAO
import com.wooriyo.pinmenuer.db.dao.MemberDAO
import com.wooriyo.pinmenuer.db.dao.StoreDAO
import com.wooriyo.pinmenuer.db.entity.Category
import com.wooriyo.pinmenuer.db.entity.Member
import com.wooriyo.pinmenuer.db.entity.Store

@Database(entities = [Member::class, Store::class, Category::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memberDAO() : MemberDAO
    abstract fun categoryDAO() : CategoryDAO
    abstract fun storeDAO() : StoreDAO

    companion object {
        private var instance : AppDatabase ?= null

        fun getInstance (context: Context) : AppDatabase {
            if(instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "PinMenuER"
                ).build()
            }
            return instance!!
        }
    }
}