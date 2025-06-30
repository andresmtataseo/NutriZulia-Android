package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.local.view.PacienteConCita
import com.nutrizulia.data.repository.collection.PacienteRepository
import javax.inject.Inject

class GetPacientesConCitas @Inject constructor(
    private val repository: PacienteRepository
) {
    suspend operator fun invoke(usuarioInstitucionId: Int): List<PacienteConCita> {
        return repository.findAllPacientesConCitas(usuarioInstitucionId)
    }
}