package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.ActividadDao
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.local.view.ActividadConTipo
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
import com.nutrizulia.data.remote.api.collection.IFullSyncService
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.BatchSyncResult
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.toBatchSyncResult
import com.nutrizulia.domain.model.toSyncResult
import java.time.LocalDateTime
import javax.inject.Inject

class ActividadRepository @Inject constructor(
    private val dao: ActividadDao,
    private val api: IBatchSyncService,
    private val fullSyncApi: IFullSyncService
) {
    suspend fun findAllNotSynced(): Int {
        return dao.countNotSynced()
    }

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

    /**
     * Sincronización completa de actividades desde el backend
     * Recupera todas las actividades del usuario y las guarda localmente
     * @return SyncResult<Int> con el número de registros procesados
     */
    suspend fun fullSyncActividades(): SyncResult<Int> {
        return try {
            android.util.Log.d("ActividadRepository", "Iniciando sincronización completa de actividades")
            
            val response = fullSyncApi.getFullSyncActividades()
            
            response.toSyncResult { fullSyncResponse ->
                android.util.Log.d("ActividadRepository", "Respuesta recibida: ${fullSyncResponse.data?.totalRegistros} actividades")
                
                if (fullSyncResponse.data?.datos!!.isNotEmpty()) {
                    // Convertir DTOs a entidades y hacer upsert
                    val entidades = fullSyncResponse.data.datos.map { it.toEntity() }
                    dao.upsertAll(entidades)
                    
                    android.util.Log.d("ActividadRepository", "Sincronización completa de actividades exitosa: ${entidades.size} registros")
                    SyncResult.Success(
                        entidades.size,
                        "Sincronización completa de actividades exitosa: ${entidades.size} registros"
                    )
                } else {
                    android.util.Log.d("ActividadRepository", "No hay actividades para sincronizar")
                    SyncResult.Success(
                        0,
                        "No hay actividades para sincronizar"
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ActividadRepository", "Error en sincronización completa de actividades", e)
            e.toSyncResult()
        }
    }

}