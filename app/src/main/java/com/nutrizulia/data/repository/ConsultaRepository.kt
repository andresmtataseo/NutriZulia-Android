package com.nutrizulia.data.repository

import com.nutrizulia.data.local.dao.ConsultaDao
import com.nutrizulia.data.local.entity.toEntity
import com.nutrizulia.domain.model.Consulta
import javax.inject.Inject

class ConsultaRepository @Inject constructor(
    private val consultaDao: ConsultaDao
){

    suspend fun insertConsulta(consulta: Consulta): Long {
        return consultaDao.insertConsulta(consulta.toEntity())
    }

}