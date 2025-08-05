package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.DiagnosticoRepository
import com.nutrizulia.domain.model.collection.Diagnostico
import javax.inject.Inject

class GetDiagnosticosByConsultaId @Inject constructor(
    private val repository: DiagnosticoRepository
) {
    suspend operator fun invoke(consultaId: String): List<Diagnostico> {
        return repository.findAllByConsultaId(consultaId)
    }
} 