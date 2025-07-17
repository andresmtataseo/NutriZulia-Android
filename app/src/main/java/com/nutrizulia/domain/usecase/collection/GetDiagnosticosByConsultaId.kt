package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.DiagnosticoRepository
import com.nutrizulia.data.local.entity.collection.DiagnosticoEntity
import javax.inject.Inject

class GetDiagnosticosByConsultaId @Inject constructor(
    private val repository: DiagnosticoRepository
) {
    suspend operator fun invoke(consultaId: String): List<DiagnosticoEntity> {
        return repository.findByConsultaId(consultaId)
    }
} 