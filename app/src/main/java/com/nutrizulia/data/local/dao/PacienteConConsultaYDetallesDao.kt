package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.nutrizulia.data.local.view.PacienteConConsultaYDetalles

@Dao
interface PacienteConConsultaYDetallesDao{
    @Query("""
        SELECT *
        FROM pacientes_con_consulta_y_detalles
        WHERE consultaUsuarioInstitucionId = :usuarioInstitucionId
        ORDER BY fechaHoraReal DESC
    """)
    suspend fun findAllConsultasByUsuarioInstitucionId(usuarioInstitucionId: String): List<PacienteConConsultaYDetalles>

}
