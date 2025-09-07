package com.nutrizulia.domain.usecase.dashboard

import com.nutrizulia.data.repository.collection.PacienteRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class GetProximasConsultasUseCase @Inject constructor(
    private val pacienteRepository: PacienteRepository
) {
    suspend operator fun invoke(usuarioInstitucionId: Int): List<ProximaConsultaData> {
        val pacientesConCitas = pacienteRepository.findAllPacientesConCitas(usuarioInstitucionId)
        
        return pacientesConCitas
            .filter { it.fechaHoraProgramadaConsulta != null }
            .filter { it.fechaHoraProgramadaConsulta!!.isAfter(LocalDateTime.now()) }
            .filter { it.estadoConsulta.name == "PENDIENTE" || it.estadoConsulta.name == "REPROGRAMADA" }
            .sortedBy { it.fechaHoraProgramadaConsulta }
            .take(5) // Mostrar solo las próximas 5 consultas
            .map { pacienteConCita ->
                ProximaConsultaData(
                    nombrePaciente = pacienteConCita.nombreCompleto,
                    fechaHora = pacienteConCita.fechaHoraProgramadaConsulta!!.format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a", Locale("es", "ES"))
                    ),
                    consultaId = pacienteConCita.consultaId
                )
            }
    }
}

data class ProximaConsultaData(
    val nombrePaciente: String,
    val fechaHora: String,
    val consultaId: String
)