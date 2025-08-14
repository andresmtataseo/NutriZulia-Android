package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetalleObstetriciaDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
import com.nutrizulia.data.remote.api.collection.IFullSyncService
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.BatchSyncResult
import com.nutrizulia.domain.model.toBatchSyncResult
import com.nutrizulia.domain.model.collection.DetalleObstetricia
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.toSyncResult
import java.time.LocalDateTime
import javax.inject.Inject

class DetalleObstetriciaRepository @Inject constructor(
    private val dao: DetalleObstetriciaDao,
    private val batchApi: IBatchSyncService,
    private val fullSyncApi: IFullSyncService
) {
    suspend fun upsert(it: DetalleObstetricia) {
        dao.upsert(it.toEntity())
    }
    suspend fun findByConsultaId(ConsultaId: String): DetalleObstetricia? {
        return dao.findByConsultaId(ConsultaId)?.toDomain()
    }

    suspend fun sincronizarDetallesObstetriciaBatch(): SyncResult<BatchSyncResult> {
        return try {
            val obstetriciasPendientes = dao.findAllNotSynced()
            if (obstetriciasPendientes.isEmpty()) {
                return SyncResult.Success(
                    BatchSyncResult(),
                    "No hay detalles obstétricos para sincronizar"
                )
            }

            val obstetriciasDto = obstetriciasPendientes.map { it.toDto() }
            val response = batchApi.syncDetallesObstetriciaBatch(obstetriciasDto)

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
                            response.body()?.message ?: "Error en la sincronización de detalles obstétricos",
                            null
                        )
                    }
                } else {
                    SyncResult.Success(
                        batchResult,
                        response.body()?.message ?: "Sincronización de detalles obstétricos completada exitosamente"
                    )
                }
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }

    /**
     * Sincronización completa de detalles obstétricos desde el backend
     * Recupera todos los detalles del usuario y los guarda localmente
     * @return SyncResult<Int> con el número de registros procesados
     */
    suspend fun fullSyncDetallesObstetricia(): SyncResult<Int> {
        return try {
            android.util.Log.d("DetalleObstetriciaRepository", "Iniciando sincronización completa de detalles obstétricos")
            
            val response = fullSyncApi.getFullSyncDetallesObstetricias()
            
            response.toSyncResult { fullSyncResponse ->
                android.util.Log.d("DetalleObstetriciaRepository", "Respuesta recibida: ${fullSyncResponse.data?.totalRegistros} detalles obstétricos")
                
                if (fullSyncResponse.data?.datos!!.isNotEmpty()) {
                    // Convertir DTOs a entidades y hacer upsert
                    val entidades = fullSyncResponse.data.datos.map { it.toEntity() }
                    dao.upsertAll(entidades)
                    
                    android.util.Log.d("DetalleObstetriciaRepository", "Sincronización completa de detalles obstétricos exitosa: ${entidades.size} registros")
                    SyncResult.Success(
                        entidades.size,
                        "Sincronización completa de detalles obstétricos exitosa: ${entidades.size} registros"
                    )
                } else {
                    android.util.Log.d("DetalleObstetriciaRepository", "No hay detalles obstétricos para sincronizar")
                    SyncResult.Success(
                        0,
                        "No hay detalles obstétricos para sincronizar"
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DetalleObstetriciaRepository", "Error en sincronización completa de detalles obstétricos", e)
            e.toSyncResult()
        }
    }
}