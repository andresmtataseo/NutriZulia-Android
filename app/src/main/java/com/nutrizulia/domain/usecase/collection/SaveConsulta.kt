package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.ConsultaRepository
import com.nutrizulia.domain.model.collection.Consulta
import javax.inject.Inject

class SaveConsulta @Inject constructor(
    private val consultaRepository: ConsultaRepository
) {
    suspend operator fun invoke(consulta: Consulta): Long {
        return consultaRepository.upsert(consulta)
    }
}