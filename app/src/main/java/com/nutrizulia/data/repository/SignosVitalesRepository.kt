package com.nutrizulia.data.repository

import com.nutrizulia.data.local.dao.SignosVitalesDao
import com.nutrizulia.data.local.entity.toEntity
import com.nutrizulia.domain.model.SignosVitales
import com.nutrizulia.domain.model.toDomain
import javax.inject.Inject

class SignosVitalesRepository@Inject constructor(
    private val signosVitalesDao: SignosVitalesDao
) {

    suspend fun insertSignosVitales(signosVitales: SignosVitales) : Long {
        return signosVitalesDao.insertSignosVitales(signosVitales.toEntity())
    }

    suspend fun getSignosVitalesByConsulta(idConsulta: Int) : SignosVitales {
        return signosVitalesDao.getSignosVitalesByConsultaId(idConsulta).toDomain()
    }

}

