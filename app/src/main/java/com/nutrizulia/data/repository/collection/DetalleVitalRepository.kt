package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetalleVitalDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.remote.api.collection.ICollectionSyncService
import com.nutrizulia.data.remote.dto.collection.DetalleVitalDto
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.collection.DetalleVital
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.toSyncResult
import java.time.LocalDateTime
import javax.inject.Inject

class DetalleVitalRepository @Inject constructor(
    private val dao: DetalleVitalDao,
    private val api: ICollectionSyncService
) {
    suspend fun upsert(detalleVital: DetalleVital) {
        dao.upsert(detalleVital.toEntity())
    }

    suspend fun findByConsultaId(consultaId: String) : DetalleVital? {
        return dao.findByConsultaId(consultaId)?.toDomain()
    }
    
    suspend fun sincronizarDetallesVitales(): SyncResult<List<DetalleVitalDto>> {
        return try {
            val detallesVitalesPendientes = dao.findAllNotSynced()
            if (detallesVitalesPendientes.isEmpty()) {
                return SyncResult.Success(emptyList(), "No hay detalles de vital para sincronizar")
            }
            val detallesVitalesDto = detallesVitalesPendientes.map { it.toDto() }
            val response = api.syncDetallesVitales(detallesVitalesDto)
            
            response.toSyncResult { apiResponse ->
                val data = apiResponse.data ?: emptyList()
                data.forEach { dto ->
                    val entity = dto.toEntity().copy(
                        isSynced = true,
                        updatedAt = LocalDateTime.now()
                    )
                    dao.upsert(entity)
                }
                SyncResult.Success(data, response.body()?.message ?: "Sincronización de detalles de vital completada")
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }
}