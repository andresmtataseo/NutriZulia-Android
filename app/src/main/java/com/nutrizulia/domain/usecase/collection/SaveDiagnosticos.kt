package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.DiagnosticoRepository
import com.nutrizulia.domain.model.collection.Diagnostico
import javax.inject.Inject

class SaveDiagnosticos @Inject constructor(
    private val repository: DiagnosticoRepository
) {
    suspend operator fun invoke(consultaId: String, diagnosticos: List<Diagnostico>) {
        // Obtener los diagnósticos existentes para la consulta
        val diagnosticosExistentes = repository.findAllByConsultaId(consultaId)
        
        // Identificar los riesgos biológicos de los diagnósticos nuevos
        val nuevosRiesgosBiologicosIds = diagnosticos.map { it.riesgoBiologicoId }.toSet()
        
        // Identificar los riesgos biológicos de los diagnósticos existentes
        val existentesRiesgosBiologicosIds = diagnosticosExistentes.map { it.riesgoBiologicoId }.toSet()
        
        // Si hay cambios en los diagnósticos (eliminaciones o adiciones)
        if (nuevosRiesgosBiologicosIds != existentesRiesgosBiologicosIds) {
            // Marcar como eliminados TODOS los diagnósticos previos de la consulta (soft delete)
            // Esto es necesario para que el servidor sepa que fueron eliminados durante la sincronización
            repository.deleteByConsultaId(consultaId)
            
            // Insertar/actualizar los nuevos diagnósticos
            // Usar upsert para evitar conflictos de clave primaria con diagnósticos eliminados
            if (diagnosticos.isNotEmpty()) {
                repository.upsertAll(diagnosticos)
            }
        }
        // Si no hay cambios, no hacemos nada para evitar duplicados
    }
}