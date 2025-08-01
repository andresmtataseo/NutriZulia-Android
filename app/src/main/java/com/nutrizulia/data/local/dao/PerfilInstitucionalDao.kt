package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.nutrizulia.data.local.view.PerfilInstitucional

@Dao
interface PerfilInstitucionalDao {

    @Query("SELECT * FROM perfiles_institucionales WHERE usuario_id = :usuarioId")
    suspend fun fillAllPerfilInstitucionalByUsuarioId(usuarioId: Int): List<PerfilInstitucional>

}