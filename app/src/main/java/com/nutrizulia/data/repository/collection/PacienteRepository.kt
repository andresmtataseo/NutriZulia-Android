package com.nutrizulia.data.repository.collection

import android.util.Log
import com.nutrizulia.data.local.dao.PacienteConCitaDao
import com.nutrizulia.data.local.dao.collection.PacienteDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.local.view.PacienteConCita
import com.nutrizulia.data.remote.api.collection.IPacienteService
import com.nutrizulia.data.remote.dto.collection.PacienteRequestDto
import com.nutrizulia.data.remote.dto.collection.toEntity
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.model.collection.toDomain
import com.nutrizulia.domain.model.collection.toDto
import javax.inject.Inject

class PacienteRepository @Inject constructor(
    private val pacienteDao: PacienteDao,
    private val pacienteConCitaDao: PacienteConCitaDao,
    private val api: IPacienteService
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

    suspend fun sincronizarPacientes() {
        val pacientesNoSincronizados = pacienteDao.findAllNotSynced()
        if (pacientesNoSincronizados.isEmpty()) return

        try {
            val pacientesDto = pacientesNoSincronizados.map { it.toDto() }
            val response = api.syncPacientes(pacientesDto)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null && body.status == 200) {
                    val data = body.data ?: emptyList()

                    // 1. Marcar como sincronizados los que enviaste
                    pacienteDao.updateAll(pacientesNoSincronizados.map {
                        it.copy(isSynced = true)
                    })

                    // 2. Sobrescribir con los que vinieron más nuevos del backend
                    data.forEach { dto ->
                        val paciente = dto.toEntity().copy(isSynced = true)
                        pacienteDao.upsert(paciente)
                    }
                } else {
                    // Respuesta con status != 200 (error de negocio)
                }
            } else {
                // Respuesta HTTP no exitosa (404, 500, etc.)
            }

        } catch (e: Exception) {
            // Aquí podrías:
            // - Mostrar mensaje al usuario
            // - Agendar retry
            // - Guardar en base local que falló el intento
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