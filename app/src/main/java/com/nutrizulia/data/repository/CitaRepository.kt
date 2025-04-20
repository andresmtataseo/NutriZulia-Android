package com.nutrizulia.data.repository

import com.nutrizulia.data.local.dao.CitaDao
import com.nutrizulia.data.local.entity.toEntity
import com.nutrizulia.domain.model.Cita
import com.nutrizulia.domain.model.toDomain
import javax.inject.Inject

class CitaRepository @Inject constructor(private val citaDao: CitaDao) {

    suspend fun getAllCitas(): List<Cita> {
        val response = citaDao.getAllCitas()
        return response.map { it.toDomain() }
    }

    suspend fun insertCita(cita: Cita): Long {
        return citaDao.insertCita(cita.toEntity() )
    }


}