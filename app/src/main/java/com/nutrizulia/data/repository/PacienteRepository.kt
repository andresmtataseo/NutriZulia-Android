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

    suspend fun insertPaciente(paciente: Paciente): Long {
        return pacienteDao.insertPaciente(paciente.toEntity() )
    }
}