package com.nutrizulia.data.repository.collection

import android.util.Log
import com.nutrizulia.data.local.dao.PacienteConCitaDao
import com.nutrizulia.data.local.dao.collection.PacienteDao
import com.nutrizulia.data.local.dao.collection.ConsultaDao
import com.nutrizulia.data.local.dao.collection.PacienteRepresentanteDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.local.view.PacienteConCita
import com.nutrizulia.data.remote.api.collection.IBatchSyncService
import com.nutrizulia.data.remote.api.collection.IFullSyncService
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
    private val consultaDao: ConsultaDao,
    private val pacienteRepresentanteDao: PacienteRepresentanteDao,
    private val batchApi: IBatchSyncService,
    private val fullSyncApi: IFullSyncService
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

    suspend fun findAllNotSynced(usuarioInstitucionId: Int): Int {
        return pacienteDao.countNotSynced(usuarioInstitucionId)
    }

    suspend fun sincronizarPacientesBatch(usuarioInstitucionId: Int): SyncResult<BatchSyncResult> {
        return try {
            val pacientesPendientes = pacienteDao.findAllNotSynced(usuarioInstitucionId)
            Log.d("PacienteRepository", "Pacientes no sincronizados encontrados: ${pacientesPendientes.size}")
            if (pacientesPendientes.isEmpty()) {
                Log.d("PacienteRepository", "No hay pacientes para sincronizar")
                return SyncResult.Success(
                    BatchSyncResult(),
                    "No hay pacientes para sincronizar"
                )
            }

            val pacientesDto = pacientesPendientes.map { it.toDto() }
            val response = batchApi.syncPacientesBatch(pacientesDto)

            response.toBatchSyncResult { batchResult ->
                batchResult.successfulUuids.forEach { uuid ->
                    pacienteDao.markAsSynced(uuid, LocalDateTime.now())
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

    /**
     * Sincronización completa de pacientes desde el backend
     * Recupera todos los pacientes del usuario y los guarda localmente
     * @param usuarioInstitucionId ID de la institución del usuario
     * @return SyncResult<Int> con el número de registros procesados
     */
    suspend fun fullSyncPacientes(): SyncResult<Int> {
        return try {
            Log.d("PacienteRepository", "Iniciando sincronización completa de pacientes")
            
            val response = fullSyncApi.getFullSyncPacientes()
            
            response.toSyncResult { fullSyncResponse ->
                android.util.Log.d("PacienteRepository", "Respuesta recibida: ${fullSyncResponse.data?.totalRegistros} pacientes")
                
                if (fullSyncResponse.data?.datos!!.isNotEmpty()) {
                    // Convertir DTOs a entidades y hacer upsert
                    val entidades = fullSyncResponse.data.datos.map { it.toEntity() }
                    pacienteDao.upsertAll(entidades)
                    
                    android.util.Log.d("PacienteRepository", "Sincronización completa de pacientes exitosa: ${entidades.size} registros")
                    SyncResult.Success(
                        entidades.size,
                        "Sincronización completa de pacientes exitosa: ${entidades.size} registros"
                    )
                } else {
                    android.util.Log.d("PacienteRepository", "No hay pacientes para sincronizar")
                    SyncResult.Success(
                        0,
                        "No hay pacientes para sincronizar"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("PacienteRepository", "Error en sincronización completa de pacientes", e)
            e.toSyncResult()
        }
    }

    /**
     * Verifica si un paciente tiene registros asociados activos (no eliminados)
     */
    suspend fun hasActiveAssociatedRecords(pacienteId: String): Boolean {
        // Verificar si hay consultas activas para este paciente
        // Las consultas son la entidad principal directamente relacionada con el paciente
        val consultaCount = consultaDao.countConsultaByPacienteId(pacienteId)
        if (consultaCount > 0) {
            return true
        }

        // Verificar si hay representantes activos asociados al paciente
        // PacienteRepresentanteEntity es la única otra entidad directamente relacionada
        val representante = pacienteRepresentanteDao.findByPacienteId(pacienteId)
        if (representante != null && !representante.isDeleted) {
            return true
        }

        // No hay registros activos asociados directamente al paciente
        return false
    }

    /**
     * Realiza un soft delete del paciente si cumple las condiciones
     * @param usuarioInstitucionId ID del usuario institución
     * @param pacienteId ID del paciente a eliminar
     * @return SoftDeleteResult indicando el resultado de la operación
     */
    suspend fun softDeletePaciente(usuarioInstitucionId: Int, pacienteId: String): SoftDeleteResult {
        return try {
            // Buscar el paciente
            val paciente = pacienteDao.findById(usuarioInstitucionId, pacienteId)
                ?: return SoftDeleteResult.Error("Paciente no encontrado")

            // Verificar que esté sincronizado
            if (!paciente.isSynced) {
                return SoftDeleteResult.Error("El paciente debe estar sincronizado para poder eliminarlo")
            }

            // Verificar que no tenga registros asociados activos
            if (hasActiveAssociatedRecords(pacienteId)) {
                return SoftDeleteResult.Error("No se puede eliminar el paciente porque tiene registros asociados activos")
            }

            // Realizar soft delete
            val updatedPaciente = paciente.copy(
                isDeleted = true,
                isSynced = false,
                updatedAt = java.time.LocalDateTime.now()
            )
            
            pacienteDao.update(updatedPaciente)
            
            SoftDeleteResult.Success("Paciente eliminado correctamente")
        } catch (e: Exception) {
            SoftDeleteResult.Error("Error al eliminar paciente: ${e.message}")
        }
    }
}

/**
 * Resultado de la operación de soft delete
 */
sealed class SoftDeleteResult {
    data class Success(val message: String) : SoftDeleteResult()
    data class Error(val message: String) : SoftDeleteResult()
}