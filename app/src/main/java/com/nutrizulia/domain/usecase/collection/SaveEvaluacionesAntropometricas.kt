package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.EvaluacionAntropometricaRepository
import com.nutrizulia.domain.model.collection.EvaluacionAntropometrica
import javax.inject.Inject

class SaveEvaluacionesAntropometricas @Inject constructor(
    private val repository: EvaluacionAntropometricaRepository
) {
    suspend operator fun invoke(consultaId: String, evaluaciones: List<EvaluacionAntropometrica>) {
        // Obtener evaluaciones existentes para comparar
        val evaluacionesExistentes = repository.findAllByConsultaId(consultaId)
        
        // Crear un mapa de evaluaciones existentes por tipo de indicador y tipo de valor
        val evaluacionesExistentesMap = evaluacionesExistentes.associateBy { 
            "${it.tipoIndicadorId}_${it.tipoValorCalculado}" 
        }
        
        // Crear un mapa de nuevas evaluaciones por tipo de indicador y tipo de valor
        val nuevasEvaluacionesMap = evaluaciones.associateBy { 
            "${it.tipoIndicadorId}_${it.tipoValorCalculado}" 
        }
        
        // Identificar evaluaciones que ya no están en la nueva lista (para marcar como eliminadas)
        val evaluacionesParaEliminar = evaluacionesExistentesMap.keys - nuevasEvaluacionesMap.keys
        
        // Marcar como eliminadas las evaluaciones que ya no están presentes
        if (evaluacionesParaEliminar.isNotEmpty()) {
            // Solo marcar como eliminadas las evaluaciones específicas que ya no están
            evaluacionesParaEliminar.forEach { key ->
                val evaluacionExistente = evaluacionesExistentesMap[key]!!
                val evaluacionEliminada = evaluacionExistente.copy(
                    isDeleted = true,
                    isSynced = false,
                    updatedAt = java.time.LocalDateTime.now()
                )
                repository.upsertAll(listOf(evaluacionEliminada))
            }
        }
        
        // Procesar nuevas evaluaciones y actualizaciones
        val evaluacionesParaUpsert = nuevasEvaluacionesMap.map { (key, nuevaEvaluacion) ->
            val evaluacionExistente = evaluacionesExistentesMap[key]
            
            if (evaluacionExistente != null) {
                // Reutilizar ID existente para actualización
                nuevaEvaluacion.copy(
                    id = evaluacionExistente.id,
                    updatedAt = java.time.LocalDateTime.now(),
                    isDeleted = false,
                    isSynced = false
                )
            } else {
                // Nueva evaluación con nuevo ID
                nuevaEvaluacion.copy(
                    updatedAt = java.time.LocalDateTime.now(),
                    isDeleted = false,
                    isSynced = false
                )
            }
        }
        
        // Usar upsert para insertar nuevas o actualizar existentes
        if (evaluacionesParaUpsert.isNotEmpty()) {
            repository.upsertAll(evaluacionesParaUpsert)
        }
    }
}