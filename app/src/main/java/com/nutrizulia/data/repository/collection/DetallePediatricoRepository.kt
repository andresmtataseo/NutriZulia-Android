package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetallePediatricoDao
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
import com.nutrizulia.data.remote.api.collection.IFullSyncService
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.BatchSyncResult
import com.nutrizulia.domain.model.toBatchSyncResult
import com.nutrizulia.domain.model.collection.DetallePediatrico
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.toSyncResult
import java.time.LocalDateTime
import javax.inject.Inject

class DetallePediatricoRepository @Inject constructor(
    private val dao: DetallePediatricoDao,
    private val batchApi: IBatchSyncService,
    private val fullSyncApi: IFullSyncService
) {
    suspend fun upsert(detallePediatrico: DetallePediatrico) {
        dao.upsert(detallePediatrico.toEntity())
    }
    suspend fun findByConsultaId(consultaId: String): DetallePediatrico? {
        return dao.findByConsultaId(consultaId)?.toDomain()
    }

    suspend fun findLatestByPacienteId(pacienteId: String): DetallePediatrico? {
        return dao.findLatestByPacienteId(pacienteId)?.toDomain()
    }

    suspend fun findAllNotSynced(usuarioInstitucionId: Int): Int {
        return dao.countNotSynced(usuarioInstitucionId)
    }

    suspend fun sincronizarDetallesPediatricosBatch(usuarioInstitucionId: Int): SyncResult<BatchSyncResult> {
        return try {
            val pediatricosPendientes = dao.findAllNotSynced(usuarioInstitucionId)
            if (pediatricosPendientes.isEmpty()) {
                return SyncResult.Success(
                    BatchSyncResult(),
                    "No hay detalles pediátricos para sincronizar"
                )
            }

            val pediatricosDto = pediatricosPendientes.map { it.toDto() }
            val response = batchApi.syncDetallesPediatricosBatch(pediatricosDto)

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
                            response.body()?.message ?: "Error en la sincronización de detalles pediátricos",
                            null
                        )
                    }
                } else {
                    SyncResult.Success(
                        batchResult,
                        response.body()?.message ?: "Sincronización de detalles pediátricos completada exitosamente"
                    )
                }
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }

    /**
     * Sincronización completa de detalles pediátricos desde el backend
     * Recupera todos los detalles del usuario y los guarda localmente
     * @param usuarioInstitucionId ID de la institución del usuario
     * @return SyncResult<Int> con el número de registros procesados
     */
    suspend fun fullSyncDetallesPediatricos(): SyncResult<Int> {
        return try {
            android.util.Log.d("DetallePediatricoRepository", "Iniciando sincronización completa de detalles pediátricos")
            
            val response = fullSyncApi.getFullSyncDetallesPediatricos()
            
            response.toSyncResult { fullSyncResponse ->
                android.util.Log.d("DetallePediatricoRepository", "Respuesta recibida: ${fullSyncResponse.data?.totalRegistros} detalles pediátricos")
                
                if (fullSyncResponse.data?.datos!!.isNotEmpty()) {
                    // Convertir DTOs a entidades y hacer upsert
                    val entidades = fullSyncResponse.data.datos.map { it.toEntity() }
                    dao.upsertAll(entidades)
                    
                    android.util.Log.d("DetallePediatricoRepository", "Sincronización completa de detalles pediátricos exitosa: ${entidades.size} registros")
                    SyncResult.Success(
                        entidades.size,
                        "Sincronización completa de detalles pediátricos exitosa: ${entidades.size} registros"
                    )
                } else {
                    android.util.Log.d("DetallePediatricoRepository", "No hay detalles pediátricos para sincronizar")
                    SyncResult.Success(
                        0,
                        "No hay detalles pediátricos para sincronizar"
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DetallePediatricoRepository", "Error en sincronización completa de detalles pediátricos", e)
            e.toSyncResult()
        }
    }
}