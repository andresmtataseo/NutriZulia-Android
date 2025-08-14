package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.ActividadDao
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.local.view.ActividadConTipo
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
import com.nutrizulia.domain.model.BatchSyncResult
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.collection.Actividad
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.toBatchSyncResult
import com.nutrizulia.domain.model.toSyncResult
import java.time.LocalDateTime
import javax.inject.Inject

class ActividadRepository @Inject constructor(
    private val dao: ActividadDao,
    private val api: IBatchSyncService
) {

    suspend fun findAll(usuarioInstitucionId: Int): List<ActividadConTipo> {
        return dao.findAllByUsuarioInstitucionId(usuarioInstitucionId)
    }

    suspend fun findAllByFiltro(usuarioInstitucionId: Int, filtro: String): List<ActividadConTipo> {
        return dao.findAllByUsuarioInstitucionIdAndFilter(usuarioInstitucionId, filtro)
    }

    suspend fun sincronizarActividadesBatch(): SyncResult<BatchSyncResult> {
        return try {
            val actividadesPendientes = dao.findAllNotSynced()
            android.util.Log.d("ActividadRepository", "Actividades no sincronizadas encontradas: ${actividadesPendientes.size}")
            if (actividadesPendientes.isEmpty()) {
                android.util.Log.d("ActividadRepository", "No hay consultas para sincronizar")
                return SyncResult.Success(
                    BatchSyncResult(),
                    "No hay consultas para sincronizar"
                )
            }

            android.util.Log.d("ActividadRepository", "Enviando ${actividadesPendientes.size} consultas al servidor")
            val actividadesDto = actividadesPendientes.map { it.toDto() }
            val response = api.syncActividadesBatch(actividadesDto)

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
                            response.body()?.message ?: "Error en la sincronización de actividades",
                            null
                        )
                    }
                } else {
                    SyncResult.Success(
                        batchResult,
                        response.body()?.message ?: "Sincronización de actividades completada exitosamente"
                    )
                }
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }

}