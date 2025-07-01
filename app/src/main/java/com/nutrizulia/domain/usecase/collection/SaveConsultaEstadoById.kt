package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.repository.collection.ConsultaRepository
import javax.inject.Inject

class SaveConsultaEstadoById @Inject constructor(
    private val repository: ConsultaRepository
) {
    suspend operator fun invoke(idConsulta: String, estado: Estado) {
        return repository.updateEstadoById(idConsulta, estado)
    }
}