package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.ConsultaRepository
import com.nutrizulia.domain.model.Consulta
import javax.inject.Inject

class InsertConsultaUseCase @Inject constructor(
    private val repository: ConsultaRepository
){
    suspend operator fun invoke(consulta: Consulta): Long {
        return repository.insertConsulta(consulta)
    }
}