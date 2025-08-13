package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.DetalleAntropometricoDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
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
    private val batchApi: IBatchSyncService
) {
    suspend fun upsert(detalleAntropometrico: DetalleAntropometrico) {
        dao.upsert(detalleAntropometrico.toEntity())
    }
    suspend fun findByConsultaId(consultaId: String): DetalleAntropometrico? {
        return dao.findByConsultaId(consultaId)?.toDomain()
    }

    suspend fun sincronizarDetallesAntropometricosBatch(): SyncResult<BatchSyncResult> {
        return try {
            val antropometricosPendientes = dao.findAllNotSynced()
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

}