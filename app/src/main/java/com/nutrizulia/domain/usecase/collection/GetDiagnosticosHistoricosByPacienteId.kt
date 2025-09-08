package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.DiagnosticoRepository
import com.nutrizulia.domain.model.collection.Diagnostico
import javax.inject.Inject

class GetDiagnosticosHistoricosByPacienteId @Inject constructor(
    private val repository: DiagnosticoRepository
) {
    suspend operator fun invoke(pacienteId: String): List<Diagnostico> {
        return repository.findHistoricosByPacienteId(pacienteId)
    }
}