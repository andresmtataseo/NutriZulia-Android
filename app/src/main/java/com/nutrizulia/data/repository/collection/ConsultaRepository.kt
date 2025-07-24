package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.ConsultaDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.domain.model.collection.Consulta
import com.nutrizulia.domain.model.collection.toDomain
import javax.inject.Inject

class ConsultaRepository @Inject constructor(
    private val consultaDao: ConsultaDao
) {
    suspend fun upsert(consulta: Consulta): Long {
        return consultaDao.upsert(consulta.toEntity())
    }
    suspend fun countConsultaByPacienteId(pacienteId: String): Boolean {
        val consultationCount: Int = consultaDao.countConsultaByPacienteId(pacienteId)
        return consultationCount > 0
    }
    suspend fun findConsultaProgramadaByPacienteId(idPaciente: String): Consulta? {
        return consultaDao.findConsultaProgramadaByPacienteId(idPaciente)?.toDomain()
    }
    suspend fun findConsultaProgramadaById(id: String): Consulta? {
        return consultaDao.findConsultaProgramadaById(id)?.toDomain()
    }
    suspend fun updateEstadoById(id: String, estado: Estado) {
        return consultaDao.updateEstadoById(id, estado)
    }
}