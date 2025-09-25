package com.nutrizulia.domain.usecase.dashboard

import com.nutrizulia.data.repository.collection.ConsultaRepository
import com.nutrizulia.data.repository.collection.PacienteRepository
import com.nutrizulia.domain.model.dashboard.ResumenMensual
import java.time.LocalDate
import java.time.Period
import javax.inject.Inject

class GetDashboardStatsUseCase @Inject constructor(
    private val consultaRepository: ConsultaRepository,
    private val pacienteRepository: PacienteRepository
) {
    suspend operator fun invoke(usuarioInstitucionId: Int): ResumenMensual {
        // Obtener consultas completadas del mes actual
        val consultasDelMes = consultaRepository.getConsultasDelMesActual(usuarioInstitucionId)
        val totalConsultas = consultasDelMes.size
        
        // Calcular estadísticas por género y edad para cada consulta (incluyendo repetidas)
        var totalHombres = 0
        var totalMujeres = 0
        var totalNinos = 0
        var totalNinas = 0
        
        val fechaActual = LocalDate.now()
        
        // Iterar sobre todas las consultas del mes (incluyendo pacientes repetidos)
        consultasDelMes.forEach { consulta ->
            val paciente = pacienteRepository.findById(usuarioInstitucionId, consulta.pacienteId)
            paciente?.let { p ->
                val edad = Period.between(p.fechaNacimiento, fechaActual).years
                val isMenorDeEdad = edad < 19
                
                when {
                    p.genero.equals("MASCULINO", ignoreCase = true) -> {
                        if (isMenorDeEdad) {
                            totalNinos++
                        } else {
                            totalHombres++
                        }
                    }
                    p.genero.equals("FEMENINO", ignoreCase = true) -> {
                        if (isMenorDeEdad) {
                            totalNinas++
                        } else {
                            totalMujeres++
                        }
                    }
                }
            }
        }
        
        return ResumenMensual(
            totalConsultas = totalConsultas,
            totalHombres = totalHombres,
            totalMujeres = totalMujeres,
            totalNinos = totalNinos,
            totalNinas = totalNinas
        )
    }
}