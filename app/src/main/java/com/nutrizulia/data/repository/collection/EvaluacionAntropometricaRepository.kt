package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.EvaluacionAntropometricaDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.BatchSyncResult
import com.nutrizulia.domain.model.toBatchSyncResult
import com.nutrizulia.domain.model.collection.EvaluacionAntropometrica
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
import com.nutrizulia.domain.model.toSyncResult
import java.time.LocalDateTime
import javax.inject.Inject

class EvaluacionAntropometricaRepository @Inject constructor(
    private val dao: EvaluacionAntropometricaDao,
    private val batchApi: IBatchSyncService
) {

    suspend fun upsertAll(evaluacionAntropometrica: List<EvaluacionAntropometrica>) {
        dao.upsertAll(evaluacionAntropometrica.map { it.toEntity() })
    }

    suspend fun insertAll(evaluacionAntropometrica: List<EvaluacionAntropometrica>) {
        dao.insertAll(evaluacionAntropometrica.map { it.toEntity() })
    }

    suspend fun upsert(evaluacionAntropometrica: EvaluacionAntropometrica) {
        dao.upsert(evaluacionAntropometrica.toEntity())
    }

    suspend fun deleteByConsultaId(consultaId: String): Int {
        return dao.deleteByConsultaId(consultaId)
    }

    suspend fun findAllByConsultaId(idConsulta: String): List<EvaluacionAntropometrica> {
        return dao.findAllByConsultaId(idConsulta).map { it.toDomain() }
    }

    suspend fun sincronizarEvaluacionesAntropometricasBatch(): SyncResult<BatchSyncResult> {
        return try {
            val evaluacionesPendientes = dao.findAllNotSynced()
            if (evaluacionesPendientes.isEmpty()) {
                return SyncResult.Success(
                    BatchSyncResult(),
                    "No hay evaluaciones antropométricas para sincronizar"
                )
            }

            val evaluacionesDto = evaluacionesPendientes.map { it.toDto() }
            val response = batchApi.syncEvaluacionesAntropometricasBatch(evaluacionesDto)

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
                            response.body()?.message ?: "Error en la sincronización de evaluaciones antropométricas",
                            null
                        )
                    }
                } else {
                    SyncResult.Success(
                        batchResult,
                        response.body()?.message ?: "Sincronización de evaluaciones antropométricas completada exitosamente"
                    )
                }
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }

}