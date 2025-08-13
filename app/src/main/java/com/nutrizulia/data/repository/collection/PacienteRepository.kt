package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.PacienteConCitaDao
import com.nutrizulia.data.local.dao.collection.PacienteDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.local.view.PacienteConCita
import com.nutrizulia.data.remote.api.collection.ICollectionSyncService
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
import com.nutrizulia.data.remote.dto.collection.PacienteDto
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.BatchSyncResult
import com.nutrizulia.domain.model.toBatchSyncResult
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.collection.toDto
import com.nutrizulia.domain.model.toSyncResult
import java.time.LocalDateTime
import javax.inject.Inject

class PacienteRepository @Inject constructor(
    private val pacienteDao: PacienteDao,
    private val pacienteConCitaDao: PacienteConCitaDao,
    private val api: ICollectionSyncService,
    private val batchApi: IBatchSyncService
) {

    suspend fun upsert(paciente: Paciente): Long {
        return pacienteDao.upsert(paciente.toEntity())
    }

    suspend fun findById(usuarioInstitucionId: Int, idPaciente: String): Paciente? {
        return pacienteDao.findById(usuarioInstitucionId, idPaciente)?.toDomain()
    }

    suspend fun findByCedula( usuarioInstitucionId: Int, cedula: String): Paciente? {
        return pacienteDao.findByCedula(usuarioInstitucionId, cedula)?.toDomain()
    }

    suspend fun findAll(usuarioInstitucionId: Int): List<Paciente> {
        return pacienteDao.findAllByUsuarioInstitucionId(usuarioInstitucionId).map { it.toDomain() }
    }

    suspend fun findAllByFiltro(usuarioInstitucionId: Int, query: String): List<Paciente> {
        return pacienteDao.findAllByUsuarioInstitucionIdAndFilter( usuarioInstitucionId, query).map { it.toDomain() }
    }

    suspend fun sincronizarPacientes(): SyncResult<List<PacienteDto>> {
        return try {
            val pacientesPendientes = pacienteDao.findAllNotSynced()
            if (pacientesPendientes.isEmpty()) {
                return SyncResult.Success(emptyList(), "No hay pacientes para sincronizar")
            }
            val pacientesDto = pacientesPendientes.map { it.toDto() }
            val response = api.syncPacientes(pacientesDto)

            response.toSyncResult { apiResponse ->
                val data = apiResponse.data ?: emptyList()
                data.forEach { dto ->
                    val entity = dto.toEntity().copy(
                        isSynced = true
                    )
                    pacienteDao.upsert(entity)
                }
                SyncResult.Success(data, response.body()?.message ?: "Sincronización de pacientes completada")
            }
        } catch (e: Exception) {
            e.toSyncResult()
        }
    }

    /**
     * Sincroniza pacientes usando el nuevo formato de respuesta por lotes
     * que maneja success/failed por UUID según los requerimientos
     */
    suspend fun sincronizarPacientesBatch(): SyncResult<BatchSyncResult> {
        return try {
            val pacientesPendientes = pacienteDao.findAllNotSynced()
            if (pacientesPendientes.isEmpty()) {
                return SyncResult.Success(
                    BatchSyncResult(),
                    "No hay pacientes para sincronizar"
                )
            }

            val pacientesDto = pacientesPendientes.map { it.toDto() }
            val response = batchApi.syncPacientesBatch(pacientesDto)

            response.toBatchSyncResult { batchResult ->
                // Marcar como sincronizados los UUIDs exitosos
                batchResult.successfulUuids.forEach { uuid ->
                    pacienteDao.markAsSynced(uuid, LocalDateTime.now())
                }

                // Los UUIDs fallidos permanecen con isSynced = false
                // para ser reintentados en la próxima sincronización

                // Determinar el resultado basado en si hay errores
                if (batchResult.failedUuids.isNotEmpty()) {
                    if (batchResult.successfulUuids.isNotEmpty()) {
                        // Éxito parcial: algunos exitosos, algunos fallidos
                        SyncResult.Success(
                            batchResult,
                            "Sincronización parcial: ${batchResult.successfulUuids.size} exitosos, ${batchResult.failedUuids.size} fallidos"
                        )
                    } else {
                        // Todos fallaron
                        SyncResult.BusinessError(
                            409,
                            response.body()?.message ?: "Error en la sincronización de pacientes",
                            null
                        )
                    }
                } else {
                    // Todos exitosos
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

    // Pacientes con citas
    suspend fun findPacienteConCitaByConsultaId(usuarioInstitucionId: Int, consultaId: String): PacienteConCita? {
        return pacienteConCitaDao.findById(usuarioInstitucionId, consultaId)
    }

    suspend fun findAllPacientesConCitas(usuarioInstitucionId: Int): List<PacienteConCita> {
        return pacienteConCitaDao.findAll(usuarioInstitucionId)
    }

    suspend fun findAllPacientesConCitasByFiltro(usuarioInstitucionId: Int, filtro: String): List<PacienteConCita> {
        return pacienteConCitaDao.findAllByFiltro(usuarioInstitucionId, filtro)
    }
}