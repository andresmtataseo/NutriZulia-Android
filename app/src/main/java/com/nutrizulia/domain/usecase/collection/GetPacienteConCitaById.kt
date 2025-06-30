package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.local.view.PacienteConCita
import com.nutrizulia.data.repository.collection.PacienteRepository
import javax.inject.Inject

class GetPacienteConCitaById @Inject constructor(
    private val repository: PacienteRepository
) {
    suspend operator fun invoke(usuarioInstitucionId: Int, idConsulta: String) : PacienteConCita? {
        return repository.findPacienteConCitaByConsultaId(usuarioInstitucionId, idConsulta)
    }
}