package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.PacienteConCitaDao
import com.nutrizulia.data.local.dao.collection.PacienteDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.local.view.PacienteConCita
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.model.collection.toDomain
import javax.inject.Inject

class PacienteRepository @Inject constructor(
    private val pacienteDao: PacienteDao,
    private val pacienteConCitaDao: PacienteConCitaDao
) {

    suspend fun insertPaciente(paciente: Paciente): Long {
        return pacienteDao.insert(paciente.toEntity())
    }

    suspend fun findById(idPaciente: String): Paciente? {
        return pacienteDao.findById(idPaciente)?.toDomain()
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

    suspend fun updatePaciente(paciente: Paciente): Int {
        return pacienteDao.update(paciente.toEntity())
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