package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.RepresentanteDao
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
import com.nutrizulia.data.remote.api.collection.IFullSyncService
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.BatchSyncResult
import com.nutrizulia.domain.model.toBatchSyncResult
import com.nutrizulia.domain.model.collection.Representante
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.toSyncResult
import java.time.LocalDateTime
import javax.inject.Inject

class RepresentanteRepository @Inject constructor(
    private val dao: RepresentanteDao,
    private val batchApi: IBatchSyncService,
    private val fullSyncApi: IFullSyncService
) {
    suspend fun findAllNotSynced(usuarioInstitucionId: Int): Int {
        return dao.countNotSynced(usuarioInstitucionId)
    }

    suspend fun findAll(idUsuarioInstitucion: Int): List<Representante> {
        return dao.findAll(idUsuarioInstitucion).map { it.toDomain() }
    }

    suspend fun findByFiltro(idUsuarioInstitucion: Int, filtro: String): List<Representante> {
        return dao.findAllByUsuarioInstitucionIdAndFilter(idUsuarioInstitucion, filtro).map { it.toDomain() }
    }

    suspend fun upsert(representante: Representante) {
        dao.upsert(representante.toEntity())
    }

    suspend fun findByCedula(usuarioInstitucionId: Int, cedula: String): Representante? {
        return dao.findByCedula(usuarioInstitucionId, cedula)?.toDomain()
    }

    suspend fun findById(usuarioInstitucionId: Int, representanteId: String): Representante? {
        return dao.findById(usuarioInstitucionId, representanteId)?.toDomain()
    }

    suspend fun sincronizarRepresentantesBatch(usuarioInstitucionId: Int): SyncResult<BatchSyncResult> {
        return try {
            val representantesPendientes = dao.findAllNotSynced(usuarioInstitucionId)
            if (representantesPendientes.isEmpty()) {
                return SyncResult.Success(
                    BatchSyncResult(),
                    "No hay representantes para sincronizar"
                )
            }

            val representantesDto = representantesPendientes.map { it.toDto() }
            val response = batchApi.syncRepresentantesBatch(representantesDto)

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
                            response.body()?.message ?: "Error en la sincronización de representantes",
                            null
                        )
                    }
                } else {
                    SyncResult.Success(
                        batchResult,
                        response.body()?.message ?: "Sincronización de representantes completada exitosamente"
                    )
                }
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }

    /**
     * Sincronización completa de representantes desde el backend
     * Recupera todos los representantes del usuario y los guarda localmente
     * @param usuarioInstitucionId ID de la institución del usuario
     * @return SyncResult<Int> con el número de registros procesados
     */
    suspend fun fullSyncRepresentantes(): SyncResult<Int> {
        return try {
            android.util.Log.d("RepresentanteRepository", "Iniciando sincronización completa de representantes")
            
            val response = fullSyncApi.getFullSyncRepresentantes()
            
            response.toSyncResult { fullSyncResponse ->
                android.util.Log.d("RepresentanteRepository", "Respuesta recibida: ${fullSyncResponse.data?.totalRegistros} representantes")

                if (fullSyncResponse.data?.datos!!.isNotEmpty()) {
                    // Convertir DTOs a entidades y hacer upsert
                    val entidades = fullSyncResponse.data.datos.map { it.toEntity() }
                    dao.upsertAll(entidades)

                    android.util.Log.d("RepresentanteRepository", "Sincronización completa de representantes exitosa: ${entidades.size} registros")
                    SyncResult.Success(
                        entidades.size,
                        "Sincronización completa de representantes exitosa: ${entidades.size} registros"
                    )
                } else {
                    android.util.Log.d("RepresentanteRepository", "No hay representantes para sincronizar")
                    SyncResult.Success(
                        0,
                        "No hay representantes para sincronizar"
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("RepresentanteRepository", "Error en sincronización completa de representantes", e)
            e.toSyncResult()
        }
    }
}