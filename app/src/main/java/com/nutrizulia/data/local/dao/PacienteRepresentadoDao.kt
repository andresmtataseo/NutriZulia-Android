package com.nutrizulia.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.nutrizulia.data.local.view.PacienteRepresentadoView
import kotlinx.coroutines.flow.Flow

@Dao
interface PacienteRepresentadoDao {

    @Query("SELECT * FROM pacientes_representados WHERE usuarioInstitucionId = :usuarioInstitucionId AND representanteId = :representanteId ORDER BY ultimaActualizacion DESC")
    suspend fun findAllByRepresentanteId(usuarioInstitucionId: Int, representanteId: String): List<PacienteRepresentadoView>

    @Query("""
        SELECT * FROM pacientes_representados 
        WHERE usuarioInstitucionId = :usuarioInstitucionId 
        AND representanteId = :representanteId 
        AND (
            pacienteNombres LIKE '%' || :query || '%' OR
            pacienteApellidos LIKE '%' || :query || '%' OR
            pacienteCedula LIKE '%' || :query || '%' OR
            parentescoNombre LIKE '%' || :query || '%'
        )
        ORDER BY ultimaActualizacion DESC
    """)
    suspend fun findAllByRepresentanteIdAndFilter(usuarioInstitucionId: Int, representanteId: String, query: String): List<PacienteRepresentadoView>

    @Query("SELECT * FROM pacientes_representados WHERE usuarioInstitucionId = :usuarioInstitucionId AND representanteId = :representanteId ORDER BY ultimaActualizacion DESC")
    fun findAllByRepresentanteIdFlow(usuarioInstitucionId: Int, representanteId: String): Flow<List<PacienteRepresentadoView>>

    @Query("SELECT * FROM pacientes_representados WHERE pacienteId = :pacienteId AND usuarioInstitucionId = :usuarioInstitucionId")
    suspend fun findByPacienteId(usuarioInstitucionId: Int, pacienteId: String): PacienteRepresentadoView?

    @Query("SELECT COUNT(*) FROM pacientes_representados WHERE usuarioInstitucionId = :usuarioInstitucionId AND representanteId = :representanteId")
    suspend fun countByRepresentanteId(usuarioInstitucionId: Int, representanteId: String): Int
}