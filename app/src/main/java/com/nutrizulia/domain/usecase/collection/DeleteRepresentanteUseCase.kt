package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.RepresentanteRepository
import com.nutrizulia.data.repository.collection.PacienteRepresentanteRepository
import com.nutrizulia.domain.usecase.collection.GetRepresentanteById
import com.nutrizulia.domain.model.collection.EliminacionResult
import javax.inject.Inject

class DeleteRepresentanteUseCase @Inject constructor(
    private val getRepresentanteById: GetRepresentanteById,
    private val representanteRepository: RepresentanteRepository,
    private val pacienteRepresentanteRepository: PacienteRepresentanteRepository
) {
    suspend operator fun invoke(representanteId: String, usuarioInstitucionId: Int): EliminacionResult {
        try {
            // 1. Verificar que el representante existe
            val representante = getRepresentanteById(usuarioInstitucionId, representanteId)
                ?: return EliminacionResult(false, "El representante no existe")

            // 2. Verificar que el representante no esté sincronizado
            if (representante.isSynced) {
                return EliminacionResult(false, "No se puede eliminar un representante sincronizado")
            }

            // 3. Verificar que no tenga registros asociados en pacientesRepresentantes
            val tieneRegistrosAsociados = pacienteRepresentanteRepository.countPacienteIdByUsuarioInstitucionIdAndRepresentanteId(
                usuarioInstitucionId, 
                representanteId
            ) > 0
            
            if (tieneRegistrosAsociados) {
                return EliminacionResult(false, "No se puede eliminar el representante porque tiene pacientes asociados")
            }

            // 4. Eliminar el representante permanentemente
            representanteRepository.delete(representante)

            return EliminacionResult(true, "Representante eliminado exitosamente")

        } catch (e: Exception) {
            return EliminacionResult(
                exitoso = false,
                mensaje = "Error al eliminar el representante: ${e.message}"
            )
        }
    }
}