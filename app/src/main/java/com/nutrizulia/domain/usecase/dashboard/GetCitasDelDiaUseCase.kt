package com.nutrizulia.domain.usecase.dashboard

import com.nutrizulia.data.repository.collection.PacienteRepository
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class GetCitasDelDiaUseCase @Inject constructor(
    private val pacienteRepository: PacienteRepository
) {
    suspend operator fun invoke(usuarioInstitucionId: Int): List<CitaDelDia> {
        val pacientesConCitas = pacienteRepository.findAllPacientesConCitas(usuarioInstitucionId)
        val today = LocalDate.now()
        
        return pacientesConCitas
            .filter { it.fechaHoraProgramadaConsulta != null }
            .filter { it.fechaHoraProgramadaConsulta!!.toLocalDate() == today }
            .sortedBy { it.fechaHoraProgramadaConsulta }
            .map { pacienteConCita ->
                CitaDelDia(
                    nombrePaciente = pacienteConCita.nombreCompleto,
                    hora = pacienteConCita.fechaHoraProgramadaConsulta!!.toLocalTime().toString(),
                    estado = pacienteConCita.estadoConsulta.name,
                    consultaId = pacienteConCita.consultaId
                )
            }
    }
}

data class CitaDelDia(
    val nombrePaciente: String,
    val hora: String,
    val estado: String,
    val consultaId: String
)