package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.local.entity.collection.DiagnosticoEntity
import com.nutrizulia.data.repository.collection.DiagnosticoRepository
import javax.inject.Inject

class SaveDiagnosticos @Inject constructor(
    private val repository: DiagnosticoRepository
) {
    suspend operator fun invoke(consultaId: String, diagnosticos: List<DiagnosticoEntity>) {
        // Eliminar los diagnósticos previos de la consulta (para edición)
        repository.deleteByConsultaId(consultaId)
        // Insertar los nuevos diagnósticos
        repository.insertAll(diagnosticos)
    }
} 