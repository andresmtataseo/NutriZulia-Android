package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.PacienteRepresentanteDao
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.BatchSyncResult
import com.nutrizulia.domain.model.collection.PacienteRepresentante
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
import com.nutrizulia.domain.model.toSyncResult
import com.nutrizulia.domain.model.toBatchSyncResult
import java.time.LocalDateTime
import javax.inject.Inject

class PacienteRepresentanteRepository @Inject constructor(
    private val pacienteRepresentanteDao: PacienteRepresentanteDao,
    private val batchApi: IBatchSyncService
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

    suspend fun sincronizarPacientesRepresentantesBatch(): SyncResult<BatchSyncResult> {
        return try {
            val pacientesRepresentantesPendientes = pacienteRepresentanteDao.findAllNotSynced()
            if (pacientesRepresentantesPendientes.isEmpty()) {
                return SyncResult.Success(
                    BatchSyncResult(),
                    "No hay pacientes-representantes para sincronizar"
                )
            }

            val pacientesRepresentantesDto = pacientesRepresentantesPendientes.map { it.toDto() }
            val response = batchApi.syncPacientesRepresentantesBatch(pacientesRepresentantesDto)

            response.toBatchSyncResult { batchResult ->
                batchResult.successfulUuids.forEach { uuid ->
                    pacienteRepresentanteDao.markAsSynced(uuid, LocalDateTime.now())
                }

                if (batchResult.failedUuids.isNotEmpty()) {
                    if (batchResult.successfulUuids.isNotEmpty()) {
                        SyncResult.Success(
                            batchResult,
                            "Sincronización parcial: ${batchResult.successfulUuids.size} exitosos, ${batchResult.failedUuids.size} fallidos"
                        )
                    } else {
                        SyncResult.BusinessError(
                            409,
                            response.body()?.message ?: "Error en la sincronización de pacientes",
                            null
                        )
                    }
                } else {
                    SyncResult.Success(
                        batchResult,
                        response.body()?.message ?: "Sincronización de pacientes completada exitosamente"
                    )
                }
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }
}