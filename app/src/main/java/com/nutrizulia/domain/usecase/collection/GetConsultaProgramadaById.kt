package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.ConsultaRepository
import com.nutrizulia.domain.model.collection.Consulta
import javax.inject.Inject

class GetConsultaProgramadaById @Inject constructor(
    private val repository: ConsultaRepository
) {
    suspend operator fun invoke(id: String): Consulta? {
        return repository.findConsultaProgramadaById(id)
    }

}