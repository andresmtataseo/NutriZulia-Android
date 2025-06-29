package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.PacienteDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.model.collection.toDomain
import javax.inject.Inject

class PacienteRepository @Inject constructor(
    private val pacienteDao: PacienteDao
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
}