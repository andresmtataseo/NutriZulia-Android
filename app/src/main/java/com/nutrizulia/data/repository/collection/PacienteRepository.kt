package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.PacienteConCitaDao
import com.nutrizulia.data.local.dao.collection.PacienteDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.local.view.PacienteConCita
import com.nutrizulia.data.remote.api.collection.ICollectionSyncService
import com.nutrizulia.data.remote.dto.collection.PacienteDto
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.collection.toDto
import javax.inject.Inject

class PacienteRepository @Inject constructor(
    private val pacienteDao: PacienteDao,
    private val pacienteConCitaDao: PacienteConCitaDao,
    private val api: ICollectionSyncService
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
        val pacientesNoSincronizados = pacienteDao.findAllNotSynced()
        if (pacientesNoSincronizados.isEmpty()) {
            return SyncResult.Success(emptyList(), "No hay pacientes para sincronizar")
        }

        return try {
            val pacientesDto = pacientesNoSincronizados.map { it.toDto() }
            val response = api.syncPacientes(pacientesDto)

            when {
                response.isSuccessful -> {
                    val body = response.body()
                    when {
                        body == null -> {
                            SyncResult.NetworkError(
                                code = response.code(),
                                message = "Respuesta vacía del servidor"
                            )
                        }
                        body.status == 200 -> {
                            val data = body.data ?: emptyList()
                            procesarSincronizacionExitosa(pacientesNoSincronizados, data)
                            val cantidadSincronizados = pacientesNoSincronizados.size
                            val mensajeCompleto = "Sincronización completada. $cantidadSincronizados pacientes sincronizados exitosamente."
                            SyncResult.Success(data, mensajeCompleto)
                        }
                        else -> {
                            SyncResult.BusinessError(
                                status = body.status,
                                message = body.message,
                                errors = body.errors
                            )
                        }
                    }
                }
                else -> {
                    SyncResult.NetworkError(
                        code = response.code(),
                        message = response.message() ?: "Error de red desconocido"
                    )
                }
            }
        } catch (e: Exception) {
            SyncResult.UnknownError(e)
        }
    }

    private suspend fun procesarSincronizacionExitosa(
        pacientesNoSincronizados: List<com.nutrizulia.data.local.entity.collection.PacienteEntity>,
        dataSincronizada: List<PacienteDto>
    ) {
        // 1. Marcar como sincronizados los que enviaste
        pacienteDao.updateAll(pacientesNoSincronizados.map {
            it.copy(isSynced = true)
        })

        // 2. Sobrescribir con los que vinieron más nuevos del backend
        dataSincronizada.forEach { dto ->
            val paciente = dto.toEntity().copy(isSynced = true)
            pacienteDao.upsert(paciente)
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