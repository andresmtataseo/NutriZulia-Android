package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetalleAntropometricoDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
import com.nutrizulia.data.remote.api.collection.IFullSyncService
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.collection.DetalleAntropometrico
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.BatchSyncResult
import com.nutrizulia.domain.model.toBatchSyncResult
import com.nutrizulia.domain.model.toSyncResult
import java.time.LocalDateTime
import javax.inject.Inject

class DetalleAntropometricoRepository @Inject constructor(
    private val dao: DetalleAntropometricoDao,
    private val batchApi: IBatchSyncService,
    private val fullSyncApi: IFullSyncService
) {
    suspend fun upsert(detalleAntropometrico: DetalleAntropometrico) {
        dao.upsert(detalleAntropometrico.toEntity())
    }
    suspend fun findByConsultaId(consultaId: String): DetalleAntropometrico? {
        return dao.findByConsultaId(consultaId)?.toDomain()
    }

    suspend fun findLatestByPacienteId(pacienteId: String): DetalleAntropometrico? {
        return dao.findLatestByPacienteId(pacienteId)?.toDomain()
    }

    suspend fun findAllNotSynced(usuarioInstitucionId: Int): Int {
        return dao.countNotSynced(usuarioInstitucionId)
    }

    suspend fun sincronizarDetallesAntropometricosBatch(usuarioInstitucionId: Int): SyncResult<BatchSyncResult> {
        return try {
            val antropometricosPendientes = dao.findAllNotSynced(usuarioInstitucionId)
            if (antropometricosPendientes.isEmpty()) {
                return SyncResult.Success(
                    BatchSyncResult(),
                    "No hay detalles antropométricos para sincronizar"
                )
            }

            val antropometricosDto = antropometricosPendientes.map { it.toDto() }
            val response = batchApi.syncDetallesAntropometricosBatch(antropometricosDto)

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
                            response.body()?.message ?: "Error en la sincronización de detalles antropométricos",
                            null
                        )
                    }
                } else {
                    SyncResult.Success(
                        batchResult,
                        response.body()?.message ?: "Sincronización de detalles antropométricos completada exitosamente"
                    )
                }
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }

    /**
     * Sincronización completa de detalles antropométricos desde el backend
     * Recupera todos los detalles del usuario y los guarda localmente
     * @param usuarioInstitucionId ID de la institución del usuario
     * @return SyncResult<Int> con el número de registros procesados
     */
    suspend fun fullSyncDetallesAntropometricos(): SyncResult<Int> {
        return try {
            android.util.Log.d("DetalleAntropometricoRepository", "Iniciando sincronización completa de detalles antropométricos")
            
            val response = fullSyncApi.getFullSyncDetallesAntropometricos()
            
            response.toSyncResult { fullSyncResponse ->
                android.util.Log.d("DetalleAntropometricoRepository", "Respuesta recibida: ${fullSyncResponse.data?.totalRegistros} detalles antropométricos")
                
                if (fullSyncResponse.data?.datos!!.isNotEmpty()) {
                    // Convertir DTOs a entidades y hacer upsert
                    val entidades = fullSyncResponse.data.datos.map { it.toEntity() }
                    dao.upsertAll(entidades)
                    
                    android.util.Log.d("DetalleAntropometricoRepository", "Sincronización completa de detalles antropométricos exitosa: ${entidades.size} registros")
                    SyncResult.Success(
                        entidades.size,
                        "Sincronización completa de detalles antropométricos exitosa: ${entidades.size} registros"
                    )
                } else {
                    android.util.Log.d("DetalleAntropometricoRepository", "No hay detalles antropométricos para sincronizar")
                    SyncResult.Success(
                        0,
                        "No hay detalles antropométricos para sincronizar"
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DetalleAntropometricoRepository", "Error en sincronización completa de detalles antropométricos", e)
            e.toSyncResult()
        }
    }

}