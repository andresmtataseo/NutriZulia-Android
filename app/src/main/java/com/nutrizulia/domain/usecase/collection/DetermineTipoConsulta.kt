package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.local.enum.TipoConsulta
import com.nutrizulia.data.repository.collection.ConsultaRepository
import javax.inject.Inject

class DetermineTipoConsulta @Inject constructor(
    private val consultaRepository: ConsultaRepository
) {
    suspend operator fun invoke(pacienteId: String): TipoConsulta {
        val hasPreviousConsultations: Boolean = consultaRepository.countConsultaByPacienteId(pacienteId)
        return if (hasPreviousConsultations) {
            TipoConsulta.CONSULTA_SUCESIVA
        } else {
            TipoConsulta.PRIMERA_CONSULTA
        }
    }
}