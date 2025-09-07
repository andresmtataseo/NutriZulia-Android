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
            .filter { pacienteConCita ->
                // Incluir consultas programadas para hoy
                (pacienteConCita.fechaHoraProgramadaConsulta != null && 
                 pacienteConCita.fechaHoraProgramadaConsulta!!.toLocalDate() == today) ||
                // Incluir consultas sin previa cita realizadas hoy
                (pacienteConCita.fechaHoraRealConsulta != null && 
                 pacienteConCita.fechaHoraRealConsulta!!.toLocalDate() == today)
            }
            .sortedBy { pacienteConCita ->
                // Ordenar por fecha programada si existe, sino por fecha real
                pacienteConCita.fechaHoraProgramadaConsulta ?: pacienteConCita.fechaHoraRealConsulta
            }
            .map { pacienteConCita ->
                val fechaHora = pacienteConCita.fechaHoraProgramadaConsulta ?: pacienteConCita.fechaHoraRealConsulta
                CitaDelDia(
                    nombrePaciente = pacienteConCita.nombreCompleto,
                    hora = fechaHora?.toLocalTime()?.toString() ?: "Sin hora",
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