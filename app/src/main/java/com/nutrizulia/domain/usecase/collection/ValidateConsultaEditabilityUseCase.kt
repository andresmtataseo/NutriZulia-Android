package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.ConsultaRepository
import javax.inject.Inject

/**
 * Caso de uso para validar si una consulta puede ser editada.
 * 
 * Reglas de negocio:
 * 1. Solo se puede editar si es la última consulta realizada en el sistema
 * 2. Solo se puede editar si no ha sido sincronizada (is_synced = false)
 */
class ValidateConsultaEditabilityUseCase @Inject constructor(
    private val consultaRepository: ConsultaRepository
) {
    
    /**
     * Valida si una consulta puede ser editada
     * 
     * @param usuarioInstitucionId ID de la institución del usuario
     * @param consultaId ID de la consulta a validar
     * @return ValidationResult con el resultado de la validación
     */
    suspend operator fun invoke(usuarioInstitucionId: Int, consultaId: String): ValidationResult {
        try {
            // 1. Obtener la consulta por ID
            val consulta = consultaRepository.findConsultaProgramadaById(consultaId)
                ?: return ValidationResult.Error("No se encontró la consulta especificada")
            
            // 2. Verificar si la consulta ya está sincronizada
            if (consulta.isSynced) {
                return ValidationResult.NotEditable("No se puede modificar una consulta que ya ha sido sincronizada con el servidor")
            }
            
            // 3. Obtener la última consulta del sistema (por fecha de actualización)
            val ultimaConsulta = consultaRepository.getUltimaConsultaRealizada(usuarioInstitucionId)
                ?: return ValidationResult.Error("No se pudo determinar la última consulta del sistema")
            
            // 4. Verificar si es la última consulta
            if (consulta.id != ultimaConsulta.id) {
                return ValidationResult.NotEditable("Solo se puede modificar la última consulta realizada en el sistema")
            }
            
            // Si pasa todas las validaciones, se puede editar
            return ValidationResult.Editable
            
        } catch (e: Exception) {
            return ValidationResult.Error("Error al validar la consulta: ${e.message}")
        }
    }
    
    /**
     * Resultado de la validación de editabilidad de una consulta
     */
    sealed class ValidationResult {
        object Editable : ValidationResult()
        data class NotEditable(val reason: String) : ValidationResult()
        data class Error(val message: String) : ValidationResult()
        
        val canEdit: Boolean
            get() = this is Editable
            
        val errorMessage: String?
            get() = when (this) {
                is NotEditable -> reason
                is Error -> message
                is Editable -> null
            }
    }
}