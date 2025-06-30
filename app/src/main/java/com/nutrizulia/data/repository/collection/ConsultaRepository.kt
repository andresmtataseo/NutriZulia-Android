package com.nutrizulia.data.repository.collection

import com.nutrizulia.data.local.dao.collection.ConsultaDao
import com.nutrizulia.data.local.entity.collection.toEntity
import com.nutrizulia.domain.model.collection.Consulta
import javax.inject.Inject

class ConsultaRepository @Inject constructor(
    private val consultaDao: ConsultaDao
) {
    suspend fun insert(consulta: Consulta): Long {
        return consultaDao.insert(consulta.toEntity())
    }
}