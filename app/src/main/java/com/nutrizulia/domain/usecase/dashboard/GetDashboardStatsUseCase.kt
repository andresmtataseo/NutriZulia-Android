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
        
        // Obtener los IDs únicos de pacientes que tuvieron consultas este mes
        val pacienteIdsDelMes = consultasDelMes.map { it.pacienteId }.distinct()
        
        // Obtener los datos de los pacientes que tuvieron consultas este mes
        val pacientesDelMes = mutableListOf<com.nutrizulia.domain.model.collection.Paciente>()
        pacienteIdsDelMes.forEach { pacienteId ->
            val paciente = pacienteRepository.findById(usuarioInstitucionId, pacienteId)
            paciente?.let { pacientesDelMes.add(it) }
        }
        
        // Calcular estadísticas por género y edad de los pacientes que tuvieron consultas este mes
        var totalHombres = 0
        var totalMujeres = 0
        var totalNinos = 0
        var totalNinas = 0
        
        val fechaActual = LocalDate.now()
        
        pacientesDelMes.forEach { paciente ->
            val edad = Period.between(paciente.fechaNacimiento, fechaActual).years
            val isMenorDeEdad = edad < 19
            
            when {
                paciente.genero.equals("MASCULINO", ignoreCase = true) -> {
                    if (isMenorDeEdad) {
                        totalNinos++
                    } else {
                        totalHombres++
                    }
                }
                paciente.genero.equals("FEMENINO", ignoreCase = true) -> {
                    if (isMenorDeEdad) {
                        totalNinas++
                    } else {
                        totalMujeres++
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