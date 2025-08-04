package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetalleAntropometricoDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.remote.api.collection.ICollectionSyncService
import com.nutrizulia.data.remote.dto.collection.DetalleAntropometricoDto
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.collection.DetalleAntropometrico
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.toSyncResult
import javax.inject.Inject

class DetalleAntropometricoRepository @Inject constructor(
    private val dao: DetalleAntropometricoDao,
    private val api: ICollectionSyncService
) {
    suspend fun upsert(detalleAntropometrico: DetalleAntropometrico) {
        dao.upsert(detalleAntropometrico.toEntity())
    }
    suspend fun findByConsultaId(consultaId: String): DetalleAntropometrico? {
        return dao.findByConsultaId(consultaId)?.toDomain()
    }
    
    suspend fun sincronizarDetallesAntropometricos(): SyncResult<List<DetalleAntropometricoDto>> {
        return try {
            val antropometricosPendientes = dao.findAllNotSynced()
            if (antropometricosPendientes.isEmpty()) {
                return SyncResult.Success(emptyList(), "No hay detalles antropometricos para sincronizar")
            }
            val antropometricosDto = antropometricosPendientes.map { it.toDto() }
            val response = api.syncDetallesAntropometricos(antropometricosDto)
            
            response.toSyncResult { apiResponse ->
                val data = apiResponse.data ?: emptyList()
                data.forEach { dto ->
                    val entity = dto.toEntity().copy(
                        isSynced = true
                    )
                    dao.upsert(entity)
                }
                SyncResult.Success(data, response.body()?.message ?: "Sincronización de detalles antropometricos completada")
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }

}