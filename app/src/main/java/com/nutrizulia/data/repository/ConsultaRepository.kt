package com.nutrizulia.data.repository

import com.nutrizulia.data.local.dao.ConsultaDao
import com.nutrizulia.data.local.dto.ConsultaConPacienteYSignosVitalesDto
import com.nutrizulia.data.local.entity.toEntity
import com.nutrizulia.domain.model.Consulta
import com.nutrizulia.domain.model.ConsultaConPacienteYSignosVitales
import com.nutrizulia.domain.model.toDomain
import javax.inject.Inject

class ConsultaRepository @Inject constructor(
    private val consultaDao: ConsultaDao
){

    suspend fun insertConsulta(consulta: Consulta): Long {
        return consultaDao.insertConsulta(consulta.toEntity())
    }

    suspend fun getConsultaByCitaId(citaId: Int): Consulta {
        return consultaDao.getConsultaByCitaId(citaId).toDomain()
    }

    suspend fun getConsultaConPacienteYSignosVitalesById(consultaId: Int): ConsultaConPacienteYSignosVitales? {
        return consultaDao.getConsultaConPacienteYSignosVitalesById(consultaId)?.toDomain()
    }

}