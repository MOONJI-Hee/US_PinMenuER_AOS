package com.wooriyo.us.pinmenuer.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.wooriyo.us.pinmenuer.db.entity.Member

@Dao
interface MemberDAO {
    @Insert
    fun insUser(member: Member)

    @Update
    fun udtMbr(member: Member)

    @Query("SELECT * FROM Member WHERE userid = :userid")
    fun chkMbr(userid : String) : List<Member>

    @Query("SELECT * FROM Member WHERE idx = :idx")
    fun findMbr(idx: Int): Member?
}