package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.ConsultaRepository
import com.nutrizulia.domain.usecase.collection.GetPacienteById
import com.nutrizulia.data.repository.collection.PacienteRepository
import com.nutrizulia.data.repository.collection.PacienteRepresentanteRepository
import com.nutrizulia.domain.model.collection.EliminacionResult
import javax.inject.Inject

class DeletePacienteUseCase @Inject constructor(
    private val getPacienteById: GetPacienteById,
    private val pacienteRepository: PacienteRepository,
    private val pacienteRepresentanteRepository: PacienteRepresentanteRepository,
    private val consultaRepository: ConsultaRepository
) {
    suspend operator fun invoke(pacienteId: String, usuarioInstitucionId: Int): EliminacionResult {
        try {
            // 1. Verificar que el paciente existe
            val paciente = getPacienteById(usuarioInstitucionId, pacienteId)
                ?: return EliminacionResult(false, "El paciente no existe")

            // 2. Verificar que el paciente no esté sincronizado
            if (paciente.isSynced) {
                return EliminacionResult(false, "No se puede eliminar un paciente sincronizado")
            }

            // 3. Verificar que no tenga consultas médicas asociadas
            val tieneConsultas = consultaRepository.countConsultaByPacienteId(pacienteId)
            if (tieneConsultas) {
                return EliminacionResult(false, "No se puede eliminar el paciente porque tiene consultas médicas asociadas")
            }

            // 4. Eliminar registros relacionados (PacienteRepresentante)
            val representante = pacienteRepresentanteRepository.findByPacienteId(pacienteId)
            representante?.let {
                pacienteRepresentanteRepository.delete(it)
            }

            // 5. Eliminar el paciente
            pacienteRepository.delete(paciente)

            return EliminacionResult(true, "Paciente eliminado exitosamente")

        } catch (e: Exception) {
            return EliminacionResult(
                exitoso = false,
                mensaje = "Error al eliminar el paciente: ${e.message}"
            )
        }
    }
}