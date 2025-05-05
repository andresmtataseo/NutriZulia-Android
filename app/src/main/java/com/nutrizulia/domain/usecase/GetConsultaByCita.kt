package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.ConsultaRepository
import javax.inject.Inject

class GetConsultaByCita @Inject constructor(
    private val repository: ConsultaRepository
) {
    suspend operator fun invoke(citaId: Int) = repository.getConsultaByCitaId(citaId)
}