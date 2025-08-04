package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetalleObstetriciaDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.remote.api.collection.ICollectionSyncService
import com.nutrizulia.data.remote.dto.collection.DetalleObstetriciaDto
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.collection.DetalleObstetricia
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.toSyncResult
import javax.inject.Inject

class DetalleObstetriciaRepository @Inject constructor(
    private val dao: DetalleObstetriciaDao,
    private val api: ICollectionSyncService
) {
    suspend fun upsert(it: DetalleObstetricia) {
        dao.upsert(it.toEntity())
    }
    suspend fun findByConsultaId(ConsultaId: String): DetalleObstetricia? {
        return dao.findByConsultaId(ConsultaId)?.toDomain()
    }
    
    suspend fun sincronizarDetallesObstetricias(): SyncResult<List<DetalleObstetriciaDto>> {
        return try {
            val obstetriciasPendientes = dao.findAllNotSynced()
            if (obstetriciasPendientes.isEmpty()) {
                return SyncResult.Success(emptyList(), "No hay detalles obstétricos para sincronizar")
            }
            val obstetriciaDto = obstetriciasPendientes.map { it.toDto() }
            val response = api.syncDetallesObstetricias(obstetriciaDto)
            
            response.toSyncResult { apiResponse ->
                val data = apiResponse.data ?: emptyList()
                data.forEach { dto ->
                    val entity = dto.toEntity().copy(
                        isSynced = true
                    )
                    dao.upsert(entity)
                }
                SyncResult.Success(data, response.body()?.message ?: "Sincronización de detalles obstétricos completada")
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }
}