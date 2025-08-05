package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.DiagnosticoRepository
import com.nutrizulia.domain.model.collection.Diagnostico
import javax.inject.Inject

class SaveDiagnosticos @Inject constructor(
    private val repository: DiagnosticoRepository
) {
    suspend operator fun invoke(consultaId: String, diagnosticos: List<Diagnostico>) {
        // Eliminar los diagnósticos previos de la consulta (para edición)
        repository.deleteByConsultaId(consultaId)
        // Insertar los nuevos diagnósticos
        repository.insertAll(diagnosticos)
    }
} 