package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.local.pojo.DiagnosticoConDescripcion
import com.nutrizulia.data.repository.collection.DiagnosticoRepository
import javax.inject.Inject

class GetDiagnosticosConDescripcionesByPacienteIdUseCase @Inject constructor(
    private val repository: DiagnosticoRepository
) {
    suspend operator fun invoke(pacienteId: String): List<DiagnosticoConDescripcion> {
        return repository.findDiagnosticosConDescripcionesByPacienteId(pacienteId)
    }
}