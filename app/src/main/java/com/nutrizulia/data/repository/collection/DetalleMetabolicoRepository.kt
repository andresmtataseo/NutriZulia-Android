package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetalleMetabolicoDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
import com.nutrizulia.data.remote.api.collection.IFullSyncService
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.BatchSyncResult
import com.nutrizulia.domain.model.toBatchSyncResult
import com.nutrizulia.domain.model.toSyncResult
import com.nutrizulia.domain.model.collection.DetalleMetabolico
import com.nutrizulia.domain.model.collection.toDomain
import java.time.LocalDateTime
import javax.inject.Inject

class DetalleMetabolicoRepository @Inject constructor(
    private val dao: DetalleMetabolicoDao,
    private val batchApi: IBatchSyncService,
    private val fullSyncApi: IFullSyncService
) {
    suspend fun upsert(detalleMetabolico: DetalleMetabolico) {
        dao.upsert(detalleMetabolico.toEntity())
    }
    suspend fun findByConsultaId(consultaId: String) : DetalleMetabolico? {
        return dao.findByConsultaId(consultaId)?.toDomain()
    }

    suspend fun sincronizarDetallesMetabolicosBatch(): SyncResult<BatchSyncResult> {
        return try {
            val metabolicosPendientes = dao.findAllNotSynced()
            if (metabolicosPendientes.isEmpty()) {
                return SyncResult.Success(
                    BatchSyncResult(),
                    "No hay detalles metabólicos para sincronizar"
                )
            }

            val metabolicosDto = metabolicosPendientes.map { it.toDto() }
            val response = batchApi.syncDetallesMetabolicosBatch(metabolicosDto)

            response.toBatchSyncResult { batchResult ->
                batchResult.successfulUuids.forEach { uuid ->
                    dao.markAsSynced(uuid, LocalDateTime.now())
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
                            response.body()?.message ?: "Error en la sincronización de detalles metabólicos",
                            null
                        )
                    }
                } else {
                    SyncResult.Success(
                        batchResult,
                        response.body()?.message ?: "Sincronización de detalles metabólicos completada exitosamente"
                    )
                }
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }

    /**
     * Sincronización completa de detalles metabólicos desde el backend
     * Recupera todos los detalles del usuario y los guarda localmente
     * @return SyncResult<Int> con el número de registros procesados
     */
    suspend fun fullSyncDetallesMetabolicos(): SyncResult<Int> {
        return try {
            android.util.Log.d("DetalleMetabolicoRepository", "Iniciando sincronización completa de detalles metabólicos")
            
            val response = fullSyncApi.getFullSyncDetallesMetabolicos()
            
            response.toSyncResult { fullSyncResponse ->
                android.util.Log.d("DetalleMetabolicoRepository", "Respuesta recibida: ${fullSyncResponse.data?.totalRegistros} detalles metabólicos")
                
                if (fullSyncResponse.data?.datos!!.isNotEmpty()) {
                    // Convertir DTOs a entidades y hacer upsert
                    val entidades = fullSyncResponse.data.datos.map { it.toEntity() }
                    dao.upsertAll(entidades)
                    
                    android.util.Log.d("DetalleMetabolicoRepository", "Sincronización completa de detalles metabólicos exitosa: ${entidades.size} registros")
                    SyncResult.Success(
                        entidades.size,
                        "Sincronización completa de detalles metabólicos exitosa: ${entidades.size} registros"
                    )
                } else {
                    android.util.Log.d("DetalleMetabolicoRepository", "No hay detalles metabólicos para sincronizar")
                    SyncResult.Success(
                        0,
                        "No hay detalles metabólicos para sincronizar"
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DetalleMetabolicoRepository", "Error en sincronización completa de detalles metabólicos", e)
            e.toSyncResult()
        }
    }

}