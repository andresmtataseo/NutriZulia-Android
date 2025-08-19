package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.local.view.PacienteConConsultaYDetalles
import com.nutrizulia.data.repository.collection.PacienteRepository
import javax.inject.Inject

class GetConsultasByPacienteIdAndFiltro @Inject constructor(
    private val repository: PacienteRepository
) {
    suspend operator fun invoke(pacienteId: String, filtro: String): List<PacienteConConsultaYDetalles> {
        return repository.getPacienteConsultaYDetallesByFiltro(pacienteId, filtro)
    }

}