package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.ConsultaRepository
import javax.inject.Inject

class ValidatePacienteCanBeEditedUseCase @Inject constructor(
    private val consultaRepository: ConsultaRepository,
) {

    suspend operator fun invoke(pacienteId: String): ValidacionResult {
        return try {
            val tieneConsultas = consultaRepository.countConsultaByPacienteId(pacienteId)
            
            if (tieneConsultas) {
                ValidacionResult(
                    puedeEditar = false,
                    mensaje = "No se puede editar la información del paciente porque tiene consultas médicas registradas."
                )
            } else {
                ValidacionResult(
                    puedeEditar = true,
                    mensaje = ""
                )
            }
            
        } catch (e: Exception) {
            ValidacionResult(
                puedeEditar = false,
                mensaje = "Error al validar la información del paciente: ${e.message}"
            )
        }
    }
}

data class ValidacionResult(
    val puedeEditar: Boolean,
    val mensaje: String
)