package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.local.view.PacienteConCita
import com.nutrizulia.data.repository.collection.PacienteRepository
import javax.inject.Inject

class GetPacientesConCitasByFiltro @Inject constructor(
    private val repository: PacienteRepository
) {
    suspend operator fun invoke(idUsuarioInstitucion: Int, filtro: String): List<PacienteConCita> {
        return repository.findAllPacientesConCitasByFiltro(idUsuarioInstitucion, filtro)
    }
}