package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.ActividadRepository
import javax.inject.Inject

class ValidateActividadEditabilityUseCase @Inject constructor(
    private val repository: ActividadRepository
) {
    suspend operator fun invoke(idActividad: String, usuarioInstitucionId: Int): ValidationResult {
        return try {
            // Verificar si la actividad existe
            val actividad = repository.findById(idActividad, usuarioInstitucionId)
                ?: return ValidationResult.NotFound("La actividad no existe")

            // Verificar si la actividad no ha sido sincronizada
            if (actividad.isSynced) {
                return ValidationResult.NotEditable("No se puede editar una actividad que ya ha sido sincronizada")
            }

            ValidationResult.Editable("La actividad puede ser editada")
        } catch (e: Exception) {
            ValidationResult.Error("Error al validar la editabilidad: ${e.message}")
        }
    }

    sealed class ValidationResult(val message: String) {
        class Editable(message: String) : ValidationResult(message)
        class NotEditable(message: String) : ValidationResult(message)
        class NotFound(message: String) : ValidationResult(message)
        class Error(message: String) : ValidationResult(message)
    }
}