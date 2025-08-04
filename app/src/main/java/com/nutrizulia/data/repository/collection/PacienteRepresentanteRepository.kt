package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.PacienteRepresentanteDao
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.remote.api.collection.ICollectionSyncService
import com.nutrizulia.data.remote.dto.collection.PacienteRepresentanteDto
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.collection.PacienteRepresentante
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.domain.model.toSyncResult
import java.time.LocalDateTime
import javax.inject.Inject

class PacienteRepresentanteRepository @Inject constructor(
    private val pacienteRepresentanteDao: PacienteRepresentanteDao,
    private val api: ICollectionSyncService
){
    suspend fun countPacienteIdByUsuarioInstitucionIdAndRepresentanteId(usuarioInstitucionId: Int, representanteId: String) : Int {
        return pacienteRepresentanteDao.countPacienteIdByUsuarioInstitucionIdAndRepresentanteId(usuarioInstitucionId, representanteId)
    }
    
    suspend fun findByPacienteId(pacienteId: String): PacienteRepresentante? {
        return pacienteRepresentanteDao.findByPacienteId(pacienteId)?.toDomain()
    }
    
    suspend fun upsert(pacienteRepresentante: PacienteRepresentante) {
        return pacienteRepresentanteDao.upsert(pacienteRepresentante.toEntity())
    }
    
    suspend fun sincronizarPacientesRepresentantes(): SyncResult<List<PacienteRepresentanteDto>> {
        return try {
            val pacientesRepresentantesPendientes = pacienteRepresentanteDao.findAllNotSynced()
            if (pacientesRepresentantesPendientes.isEmpty()) {
                return SyncResult.Success(emptyList(), "No hay pacientes-representantes para sincronizar")
            }
            val pacientesRepresentantesDto = pacientesRepresentantesPendientes.map { it.toDto() }
            val response = api.syncPacientesRepresentantes(pacientesRepresentantesDto)
            
            response.toSyncResult { apiResponse ->
                val data = apiResponse.data ?: emptyList()
                data.forEach { dto ->
                    val entity = dto.toEntity().copy(
                        isSynced = true,
                        updatedAt = LocalDateTime.now()
                    )
                    pacienteRepresentanteDao.upsert(entity)
                }
                SyncResult.Success(data, response.body()?.message ?: "Sincronización de pacientes-representantes completada")
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }
}