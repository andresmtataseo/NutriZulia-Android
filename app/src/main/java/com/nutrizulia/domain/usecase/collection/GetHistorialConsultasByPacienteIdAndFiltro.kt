package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.local.view.PacienteConCita
import com.nutrizulia.data.repository.collection.PacienteRepository
import javax.inject.Inject

class GetHistorialConsultasByPacienteIdAndFiltro @Inject constructor(
    private val pacienteRepository: PacienteRepository
) {
    suspend operator fun invoke(pacienteId: String, filtro: String): List<PacienteConCita> {
        return pacienteRepository.findHistorialPacienteConCitasByFiltro(pacienteId, filtro)
    }
}