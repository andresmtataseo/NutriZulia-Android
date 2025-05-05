package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nutrizulia.data.local.entity.SignosVitalesEntity

@Dao
interface SignosVitalesDao {

    @Query("SELECT * FROM signos_vitales WHERE consulta_id = :consultaId")
    suspend fun getSignosVitalesByConsultaId(consultaId: Int): SignosVitalesEntity

    @Insert
    suspend fun insertSignosVitales(signosVitales: SignosVitalesEntity): Long

}