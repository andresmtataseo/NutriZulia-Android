package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.ActividadRepository
import javax.inject.Inject

class DeleteActividadUseCase @Inject constructor(
    private val repository: ActividadRepository
) {
    suspend operator fun invoke(idActividad: String, usuarioInstitucionId: Int): EliminacionResult {
        return try {
            // Verificar si la actividad existe
            val actividad = repository.findById(idActividad, usuarioInstitucionId)
                ?: return EliminacionResult(
                    success = false,
                    message = "La actividad no existe"
                )

            // Verificar si la actividad no ha sido sincronizada
            if (actividad.isSynced) {
                return EliminacionResult(
                    success = false,
                    message = "No se puede eliminar una actividad que ya ha sido sincronizada"
                )
            }

            // Eliminar la actividad permanentemente
            repository.delete(actividad)

            EliminacionResult(
                success = true,
                message = "Actividad eliminada exitosamente"
            )
        } catch (e: Exception) {
            EliminacionResult(
                success = false,
                message = "Error al eliminar la actividad: ${e.message}"
            )
        }
    }

    data class EliminacionResult(
        val success: Boolean,
        val message: String
    )
}