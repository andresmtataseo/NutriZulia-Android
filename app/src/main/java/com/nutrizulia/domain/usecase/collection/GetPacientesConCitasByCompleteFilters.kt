package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.local.view.PacienteConCita
import com.nutrizulia.data.repository.collection.PacienteRepository
import javax.inject.Inject

class GetPacientesConCitasByCompleteFilters @Inject constructor(
    private val pacienteRepository: PacienteRepository
) {
    suspend operator fun invoke(
        usuarioInstitucionId: Int,
        estados: List<String>?,
        tiposConsulta: List<String>?,
        fechaInicio: String?,
        fechaFin: String?
    ): List<PacienteConCita> {
        return pacienteRepository.findAllPacientesConCitasByCompleteFilters(
            usuarioInstitucionId,
            estados,
            tiposConsulta,
            fechaInicio,
            fechaFin
        )
    }
}