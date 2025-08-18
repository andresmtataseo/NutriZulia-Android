package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.PacienteRepresentanteDao
import com.nutrizulia.data.local.dao.PacienteRepresentadoDao
import com.nutrizulia.data.local.entity.collection.toDto
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.BatchSyncResult
import com.nutrizulia.domain.model.collection.PacienteRepresentante
import com.nutrizulia.domain.model.collection.PacienteRepresentado
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
import com.nutrizulia.data.remote.api.collection.IFullSyncService
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.toSyncResult
import com.nutrizulia.domain.model.toBatchSyncResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class PacienteRepresentanteRepository @Inject constructor(
    private val pacienteRepresentanteDao: PacienteRepresentanteDao,
    private val pacienteRepresentadoDao: PacienteRepresentadoDao,
    private val batchApi: IBatchSyncService,
    private val fullSyncApi: IFullSyncService
){
    suspend fun countPacienteIdByUsuarioInstitucionIdAndRepresentanteId(usuarioInstitucionId: Int, representanteId: String) : Int {
        return pacienteRepresentanteDao.countPacienteIdByUsuarioInstitucionIdAndRepresentanteId(usuarioInstitucionId, representanteId)
    }
    
    suspend fun findByPacienteId(pacienteId: String): PacienteRepresentante? {
        return pacienteRepresentanteDao.findByPacienteId(pacienteId)?.toDomain()
    }
    
    suspend fun upsert(pacienteRepresentante: PacienteRepresentante) {
        return pacienteRepresentanteDao.upsert(pacienteRepresentante.toEntity())
    }

    // Métodos para PacienteRepresentado
    suspend fun findAllPacientesRepresentadosByRepresentanteId(usuarioInstitucionId: Int, representanteId: String): List<PacienteRepresentado> {
        return pacienteRepresentadoDao.findAllByRepresentanteId(usuarioInstitucionId, representanteId).map { it.toDomain() }
    }

    suspend fun findAllPacientesRepresentadosByRepresentanteIdAndFilter(usuarioInstitucionId: Int, representanteId: String, query: String): List<PacienteRepresentado> {
        return pacienteRepresentadoDao.findAllByRepresentanteIdAndFilter(usuarioInstitucionId, representanteId, query).map { it.toDomain() }
    }

    fun findAllPacientesRepresentadosByRepresentanteIdFlow(usuarioInstitucionId: Int, representanteId: String): Flow<List<PacienteRepresentado>> {
        return pacienteRepresentadoDao.findAllByRepresentanteIdFlow(usuarioInstitucionId, representanteId).map { list -> list.map { it.toDomain() } }
    }

    suspend fun findPacienteRepresentadoByPacienteId(usuarioInstitucionId: Int, pacienteId: String): PacienteRepresentado? {
        return pacienteRepresentadoDao.findByPacienteId(usuarioInstitucionId, pacienteId)?.toDomain()
    }

    suspend fun countPacientesRepresentadosByRepresentanteId(usuarioInstitucionId: Int, representanteId: String): Int {
        return pacienteRepresentadoDao.countByRepresentanteId(usuarioInstitucionId, representanteId)
    }

    suspend fun findAllNotSynced(usuarioInstitucionId: Int): Int {
        return pacienteRepresentanteDao.countNotSynced(usuarioInstitucionId)
    }

    suspend fun sincronizarPacientesRepresentantesBatch(usuarioInstitucionId: Int): SyncResult<BatchSyncResult> {
        return try {
            val pacientesRepresentantesPendientes = pacienteRepresentanteDao.findAllNotSynced(usuarioInstitucionId)
            if (pacientesRepresentantesPendientes.isEmpty()) {
                return SyncResult.Success(
                    BatchSyncResult(),
                    "No hay pacientes-representantes para sincronizar"
                )
            }

            val pacientesRepresentantesDto = pacientesRepresentantesPendientes.map { it.toDto() }
            val response = batchApi.syncPacientesRepresentantesBatch(pacientesRepresentantesDto)

            response.toBatchSyncResult { batchResult ->
                batchResult.successfulUuids.forEach { uuid ->
                    pacienteRepresentanteDao.markAsSynced(uuid, LocalDateTime.now())
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
                            response.body()?.message ?: "Error en la sincronización de pacientes",
                            null
                        )
                    }
                } else {
                    SyncResult.Success(
                        batchResult,
                        response.body()?.message ?: "Sincronización de pacientes completada exitosamente"
                    )
                }
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }

    /**
     * Sincronización completa de pacientes-representantes desde el backend
     * Recupera todas las relaciones del usuario y las guarda localmente
     * @param usuarioInstitucionId ID de la institución del usuario
     * @return SyncResult<Int> con el número de registros procesados
     */
    suspend fun fullSyncPacientesRepresentantes(): SyncResult<Int> {
        return try {
            android.util.Log.d("PacienteRepresentanteRepository", "Iniciando sincronización completa de pacientes-representantes")
            
            val response = fullSyncApi.getFullSyncPacientesRepresentantes()
            
            response.toSyncResult { fullSyncResponse ->
                android.util.Log.d("PacienteRepresentanteRepository", "Respuesta recibida: ${fullSyncResponse.data?.totalRegistros} relaciones paciente-representante")
                
                if (fullSyncResponse.data?.datos!!.isNotEmpty()) {
                    // Convertir DTOs a entidades y hacer upsert
                    val entidades = fullSyncResponse.data.datos.map { it.toEntity() }
                    pacienteRepresentanteDao.upsertAll(entidades)
                    
                    android.util.Log.d("PacienteRepresentanteRepository", "Sincronización completa de relaciones paciente-representante exitosa: ${entidades.size} registros")
                    SyncResult.Success(
                        entidades.size,
                        "Sincronización completa de relaciones paciente-representante exitosa: ${entidades.size} registros"
                    )
                } else {
                    android.util.Log.d("PacienteRepresentanteRepository", "No hay relaciones paciente-representante para sincronizar")
                    SyncResult.Success(
                        0,
                        "No hay relaciones paciente-representante para sincronizar"
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("PacienteRepresentanteRepository", "Error en sincronización completa de pacientes-representantes", e)
            e.toSyncResult()
        }
    }
}