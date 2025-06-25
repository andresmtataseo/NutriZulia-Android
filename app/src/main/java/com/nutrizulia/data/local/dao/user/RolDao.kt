package com.nutrizulia.data.local.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nutrizulia.data.local.entity.user.RolEntity

@Dao
interface RolDao {

    @Query("SELECT * FROM roles")
    suspend fun findAll(): List<RolEntity>

    @Insert
    suspend fun insertAll(roles: List<RolEntity>): List<Long>

    @Query("DELETE FROM roles")
    suspend fun deleteAll(): Int

}