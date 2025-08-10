package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.EvaluacionAntropometricaRepository
import com.nutrizulia.domain.model.collection.EvaluacionAntropometrica
import javax.inject.Inject

class SaveEvaluacionesAntropometricas @Inject constructor(
    private val repository: EvaluacionAntropometricaRepository
) {
    suspend operator fun invoke(consultaId: String, evaluaciones: List<EvaluacionAntropometrica>) {
        // Eliminar las evaluaciones previas de la consulta (para edición)
        repository.deleteByConsultaId(consultaId)
        // Insertar las nuevas evaluaciones
        repository.insertAll(evaluaciones)
    }
}