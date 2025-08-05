package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DiagnosticoDao
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.remote.api.collection.ICollectionSyncService
import com.nutrizulia.data.remote.dto.collection.DiagnosticoDto
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.collection.Diagnostico
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.toSyncResult
import java.time.LocalDateTime
import javax.inject.Inject

class DiagnosticoRepository @Inject constructor(
    private val dao: DiagnosticoDao,
    private val api: ICollectionSyncService
) {
    suspend fun insertAll(diagnosticos: List<Diagnostico>): List<Long> {
        return dao.insertAll(diagnosticos.map { it.toEntity() })
    }
    suspend fun deleteByConsultaId(consultaId: String): Int = dao.deleteByConsultaId(consultaId)

    suspend fun findAllByConsultaId(consultaId: String): List<Diagnostico> {
        return dao.findByConsultaId(consultaId).map { it.toDomain() }
    }
    
    suspend fun sincronizarDiagnosticos(): SyncResult<List<DiagnosticoDto>> {
        return try {
            val diagnosticosPendientes = dao.findAllNotSynced()
            if (diagnosticosPendientes.isEmpty()) {
                return SyncResult.Success(emptyList(), "No hay diagnosticos para sincronizar")
            }
            val diagnosticosDto = diagnosticosPendientes.map { it.toDto() }
            val response = api.syncDiagnosticoDto(diagnosticosDto)
            
            response.toSyncResult { apiResponse ->
                val data = apiResponse.data ?: emptyList()
                data.forEach { dto ->
                    val entity = dto.toEntity().copy(
                        isSynced = true,
                        updatedAt = LocalDateTime.now()
                    )
                    dao.upsert(entity)
                }
                SyncResult.Success(data, response.body()?.message ?: "Sincronización de diagnosticos completada")
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }
}