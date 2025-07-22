package com.nutrizulia.data.local.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.user.InstitucionEntity

@Dao
interface InstitucionDao {

    @Query("SELECT * FROM instituciones WHERE id = :id")
    suspend fun findAllById(id: Int): List<InstitucionEntity>

    @Insert
    suspend fun insertAll(instituciones: List<InstitucionEntity>): List<Long>

    @Upsert
    suspend fun upsertAll(instituciones: List<InstitucionEntity>): List<Long>

    @Query("DELETE FROM instituciones")
    suspend fun deleteAll(): Int

}