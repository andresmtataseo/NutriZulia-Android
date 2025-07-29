package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.PacienteRepresentanteRepository
import com.nutrizulia.domain.model.collection.PacienteRepresentante
import javax.inject.Inject

class GetPacienteRepresentanteByPacienteId @Inject constructor(
    private val repository: PacienteRepresentanteRepository
) {
    suspend operator fun invoke(pacienteId: String): PacienteRepresentante? {
        return repository.findByPacienteId(pacienteId)
    }
}