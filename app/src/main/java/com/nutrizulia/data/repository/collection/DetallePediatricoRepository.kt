package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetallePediatricoDao
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.remote.api.collection.ICollectionSyncService
import com.nutrizulia.data.remote.dto.collection.DetallePediatricoDto
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.collection.DetallePediatrico
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.toSyncResult
import javax.inject.Inject

class DetallePediatricoRepository @Inject constructor(
    private val dao: DetallePediatricoDao,
    private val api: ICollectionSyncService
) {
    suspend fun upsert(detallePediatrico: DetallePediatrico) {
        dao.upsert(detallePediatrico.toEntity())
    }
    suspend fun findByConsultaId(consultaId: String) : DetallePediatrico? {
        return dao.findByConsultaId(consultaId)?.toDomain()
    }
    
    suspend fun sincronizarDetallesPediatricos(): SyncResult<List<DetallePediatricoDto>> {
        return try {
            val pediatricosPendientes = dao.findAllNotSynced()
            if (pediatricosPendientes.isEmpty()) {
                return SyncResult.Success(emptyList(), "No hay detalles pediatricos para sincronizar")
            }
            val pediatricosDto = pediatricosPendientes.map { it.toDto() }
            val response = api.syncDetallesPediatricos(pediatricosDto)

            response.toSyncResult { apiResponse ->
                val data = apiResponse.data ?: emptyList()
                data.forEach { dto ->
                    val entity = dto.toEntity().copy(
                        isSynced = true
                    )
                    dao.upsert(entity)
                }
                SyncResult.Success(data, response.body()?.message ?: "Sincronización de detalles pediatricos completada")
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }
}