package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetalleMetabolicoDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.remote.api.collection.ICollectionSyncService
import com.nutrizulia.data.remote.dto.collection.DetalleMetabolicoDto
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.toSyncResult
import com.nutrizulia.domain.model.collection.DetalleMetabolico
import com.nutrizulia.domain.model.collection.toDomain
import javax.inject.Inject

class DetalleMetabolicoRepository @Inject constructor(
    private val dao: DetalleMetabolicoDao,
    private val api: ICollectionSyncService
) {
    suspend fun upsert(detalleMetabolico: DetalleMetabolico) {
        dao.upsert(detalleMetabolico.toEntity())
    }
    suspend fun findByConsultaId(consultaId: String) : DetalleMetabolico? {
        return dao.findByConsultaId(consultaId)?.toDomain()
    }
    
    suspend fun sincronizarDetallesMetabolicos(): SyncResult<List<DetalleMetabolicoDto>> {
        return try {
            val metabolicosPendientes = dao.findAllNotSynced()
            if (metabolicosPendientes.isEmpty()) {
                return SyncResult.Success(emptyList(), "No hay detalles metabolicos para sincronizar")
            }
            val metabolicoDto = metabolicosPendientes.map { it.toDto() }
            val response = api.syncDetallesMetabolicos(metabolicoDto)
            
            response.toSyncResult { apiResponse ->
                val data = apiResponse.data ?: emptyList()
                data.forEach { dto ->
                    val entity = dto.toEntity().copy(
                        isSynced = true
                    )
                    dao.upsert(entity)
                }
                SyncResult.Success(data, response.body()?.message ?: "Sincronización de detalles metabolicos completada")
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }

}