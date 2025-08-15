package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetalleVitalDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
import com.nutrizulia.data.remote.api.collection.IFullSyncService
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.collection.DetalleVital
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.BatchSyncResult
import com.nutrizulia.domain.model.toBatchSyncResult
import com.nutrizulia.domain.model.toSyncResult
import java.time.LocalDateTime
import javax.inject.Inject

class DetalleVitalRepository @Inject constructor(
    private val dao: DetalleVitalDao,
    private val batchApi: IBatchSyncService,
    private val fullSyncApi: IFullSyncService
) {
    suspend fun upsert(detalleVital: DetalleVital) {
        dao.upsert(detalleVital.toEntity())
    }

    suspend fun findByConsultaId(consultaId: String) : DetalleVital? {
        return dao.findByConsultaId(consultaId)?.toDomain()
    }

    suspend fun findAllNotSynced(): Int {
        return dao.countNotSynced()
    }

    suspend fun sincronizarDetallesVitalesBatch(): SyncResult<BatchSyncResult> {
        return try {
            val detallesVitalesPendientes = dao.findAllNotSynced()
            if (detallesVitalesPendientes.isEmpty()) {
                return SyncResult.Success(
                    BatchSyncResult(),
                    "No hay detalles vitales para sincronizar"
                )
            }

            val detallesVitalesDto = detallesVitalesPendientes.map { it.toDto() }
            val response = batchApi.syncDetallesVitalesBatch(detallesVitalesDto)

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
                            response.body()?.message ?: "Error en la sincronización de detalles vitales",
                            null
                        )
                    }
                } else {
                    SyncResult.Success(
                        batchResult,
                        response.body()?.message ?: "Sincronización de detalles vitales completada exitosamente"
                    )
                }
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }

    /**
     * Sincronización completa de detalles vitales desde el backend
     * Recupera todos los detalles del usuario y los guarda localmente
     * @return SyncResult<Int> con el número de registros procesados
     */
    suspend fun fullSyncDetallesVitales(): SyncResult<Int> {
        return try {
            android.util.Log.d("DetalleVitalRepository", "Iniciando sincronización completa de detalles vitales")
            
            val response = fullSyncApi.getFullSyncDetallesVitales()
            
            response.toSyncResult { fullSyncResponse ->
                android.util.Log.d("DetalleVitalRepository", "Respuesta recibida: ${fullSyncResponse.data?.totalRegistros} detalles vitales")
                
                if (fullSyncResponse.data?.datos!!.isNotEmpty()) {
                    // Convertir DTOs a entidades y hacer upsert
                    val entidades = fullSyncResponse.data.datos.map { it.toEntity() }
                    dao.upsertAll(entidades)
                    
                    android.util.Log.d("DetalleVitalRepository", "Sincronización completa de detalles vitales exitosa: ${entidades.size} registros")
                    SyncResult.Success(
                        entidades.size,
                        "Sincronización completa de detalles vitales exitosa: ${entidades.size} registros"
                    )
                } else {
                    android.util.Log.d("DetalleVitalRepository", "No hay detalles vitales para sincronizar")
                    SyncResult.Success(
                        0,
                        "No hay detalles vitales para sincronizar"
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DetalleVitalRepository", "Error en sincronización completa de detalles vitales", e)
            e.toSyncResult()
        }
    }
}