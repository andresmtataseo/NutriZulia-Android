package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DiagnosticoDao
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
import com.nutrizulia.data.remote.api.collection.IFullSyncService
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.BatchSyncResult
import com.nutrizulia.domain.model.toBatchSyncResult
import com.nutrizulia.domain.model.collection.Diagnostico
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.toSyncResult
import java.time.LocalDateTime
import javax.inject.Inject

class DiagnosticoRepository @Inject constructor(
    private val dao: DiagnosticoDao,
    private val batchApi: IBatchSyncService,
    private val fullSyncApi: IFullSyncService
) {
    suspend fun insertAll(diagnosticos: List<Diagnostico>): List<Long> {
        return dao.insertAll(diagnosticos.map { it.toEntity() })
    }
    
    suspend fun upsertAll(diagnosticos: List<Diagnostico>) {
        dao.upsertAll(diagnosticos.map { it.toEntity() })
    }
    
    suspend fun deleteByConsultaId(consultaId: String): Int = dao.deleteByConsultaId(consultaId)

    suspend fun findAllByConsultaId(consultaId: String): List<Diagnostico> {
        return dao.findByConsultaId(consultaId).map { it.toDomain() }
    }

    suspend fun sincronizarDiagnosticosBatch(): SyncResult<BatchSyncResult> {
        return try {
            val diagnosticosPendientes = dao.findAllNotSynced()
            if (diagnosticosPendientes.isEmpty()) {
                return SyncResult.Success(
                    BatchSyncResult(),
                    "No hay diagnósticos para sincronizar"
                )
            }

            val diagnosticosDto = diagnosticosPendientes.map { it.toDto() }
            val response = batchApi.syncDiagnosticosBatch(diagnosticosDto)

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
                            response.body()?.message ?: "Error en la sincronización de diagnósticos",
                            null
                        )
                    }
                } else {
                    SyncResult.Success(
                        batchResult,
                        response.body()?.message ?: "Sincronización de diagnósticos completada exitosamente"
                    )
                }
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }

    /**
     * Sincronización completa de diagnósticos desde el backend
     * Recupera todos los diagnósticos del usuario y los guarda localmente
     * @return SyncResult<Int> con el número de registros procesados
     */
    suspend fun fullSyncDiagnosticos(): SyncResult<Int> {
        return try {
            android.util.Log.d("DiagnosticoRepository", "Iniciando sincronización completa de diagnósticos")
            
            val response = fullSyncApi.getFullSyncDiagnosticos()
            
            response.toSyncResult { fullSyncResponse ->
                android.util.Log.d("DiagnosticoRepository", "Respuesta recibida: ${fullSyncResponse.data?.totalRegistros} diagnósticos")
                
                if (fullSyncResponse.data?.datos!!.isNotEmpty()) {
                    // Convertir DTOs a entidades y hacer upsert
                    val entidades = fullSyncResponse.data.datos.map { it.toEntity() }
                    dao.upsertAll(entidades)
                    
                    android.util.Log.d("DiagnosticoRepository", "Sincronización completa de diagnósticos exitosa: ${entidades.size} registros")
                    SyncResult.Success(
                        entidades.size,
                        "Sincronización completa de diagnósticos exitosa: ${entidades.size} registros"
                    )
                } else {
                    android.util.Log.d("DiagnosticoRepository", "No hay diagnósticos para sincronizar")
                    SyncResult.Success(
                        0,
                        "No hay diagnósticos para sincronizar"
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DiagnosticoRepository", "Error en sincronización completa de diagnósticos", e)
            e.toSyncResult()
        }
    }
}