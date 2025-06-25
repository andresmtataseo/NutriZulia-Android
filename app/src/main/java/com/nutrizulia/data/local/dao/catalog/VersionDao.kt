package com.nutrizulia.data.local.dao.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.nutrizulia.data.local.entity.catalog.VersionEntity

@Dao
interface VersionDao {

    @Query("SELECT * FROM version WHERE nombre_tabla = :nombre LIMIT 1")
    suspend fun findByNombre(nombre: String): VersionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(version: VersionEntity)

    @Query("DELETE FROM version")
    suspend fun deleteAll()

}
