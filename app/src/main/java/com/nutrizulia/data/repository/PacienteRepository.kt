package com.nutrizulia.data.repository

import com.nutrizulia.data.local.dao.PacienteDao
import com.nutrizulia.data.local.entity.toEntity
import com.nutrizulia.domain.model.Paciente
import com.nutrizulia.domain.model.toDomain
import javax.inject.Inject

class PacienteRepository @Inject constructor(private val pacienteDao: PacienteDao) {

    suspend fun getAllPacientes(): List<Paciente> {
        val response = pacienteDao.getAllPacientes()
        return response.map { it.toDomain() }
    }

    suspend fun getPacienteById(id: Int): Paciente? {
        return pacienteDao.getPacienteById(id)?.toDomain()
    }

    suspend fun getPacienteByCedula(cedula: String): Paciente? {
        return pacienteDao.getPacienteByCedula(cedula)?.toDomain()
    }

    suspend fun getPacienteByCorreo(correo: String): Paciente? {
        return pacienteDao.getPacienteByCorreo(correo)?.toDomain()
    }

    suspend fun getPacienteByTelefono(telefono: String): Paciente? {
        return pacienteDao.getPacienteByTelefono(telefono)?.toDomain()
    }

    suspend fun insertPaciente(paciente: Paciente): Long {
        return pacienteDao.insertPaciente(paciente.toEntity() )
    }
}