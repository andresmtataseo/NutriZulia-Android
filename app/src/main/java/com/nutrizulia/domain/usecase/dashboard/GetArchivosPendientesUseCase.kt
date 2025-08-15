package com.nutrizulia.domain.usecase.dashboard

import com.nutrizulia.data.repository.collection.PacienteRepository
import com.nutrizulia.data.repository.collection.ConsultaRepository
import com.nutrizulia.data.repository.collection.ActividadRepository
import com.nutrizulia.data.repository.collection.RepresentanteRepository
import com.nutrizulia.data.repository.collection.PacienteRepresentanteRepository
import com.nutrizulia.data.repository.collection.DiagnosticoRepository
import com.nutrizulia.data.repository.collection.DetalleAntropometricoRepository
import com.nutrizulia.data.repository.collection.DetalleMetabolicoRepository
import com.nutrizulia.data.repository.collection.DetalleObstetriciaRepository
import com.nutrizulia.data.repository.collection.DetallePediatricoRepository
import com.nutrizulia.data.repository.collection.DetalleVitalRepository
import com.nutrizulia.data.repository.collection.EvaluacionAntropometricaRepository
import javax.inject.Inject

class GetArchivosPendientesUseCase @Inject constructor(
    private val pacienteRepository: PacienteRepository,
    private val consultaRepository: ConsultaRepository,
    private val actividadRepository: ActividadRepository,
    private val representanteRepository: RepresentanteRepository,
    private val pacienteRepresentanteRepository: PacienteRepresentanteRepository,
    private val diagnosticoRepository: DiagnosticoRepository,
    private val detalleAntropometricoRepository: DetalleAntropometricoRepository,
    private val detalleMetabolicoRepository: DetalleMetabolicoRepository,
    private val detalleObstetriciaRepository: DetalleObstetriciaRepository,
    private val detallePediatricoRepository: DetallePediatricoRepository,
    private val detalleVitalRepository: DetalleVitalRepository,
    private val evaluacionAntropometricaRepository: EvaluacionAntropometricaRepository
) {
    suspend operator fun invoke(): Int {
        return try {
            val pacientesPendientes = pacienteRepository.findAllNotSynced()
            val consultasPendientes = consultaRepository.findAllNotSynced()
            val actividadesPendientes = actividadRepository.findAllNotSynced()
            val representantesPendientes = representanteRepository.findAllNotSynced()
            val pacienteRepresentantePendientes = pacienteRepresentanteRepository.findAllNotSynced()
            val diagnosticosPendientes = diagnosticoRepository.findAllNotSynced()
            val detalleAntropometricoPendientes = detalleAntropometricoRepository.findAllNotSynced()
            val detalleMetabolicoPendientes = detalleMetabolicoRepository.findAllNotSynced()
            val detalleObstetriciaPendientes = detalleObstetriciaRepository.findAllNotSynced()
            val detallePediatricoPendientes = detallePediatricoRepository.findAllNotSynced()
            val detalleVitalPendientes = detalleVitalRepository.findAllNotSynced()
            val evaluacionAntropometricaPendientes = evaluacionAntropometricaRepository.findAllNotSynced()
            
            listOf(
                pacientesPendientes,
                consultasPendientes,
                actividadesPendientes,
                representantesPendientes,
                pacienteRepresentantePendientes,
                diagnosticosPendientes,
                detalleAntropometricoPendientes,
                detalleMetabolicoPendientes,
                detalleObstetriciaPendientes,
                detallePediatricoPendientes,
                detalleVitalPendientes,
                evaluacionAntropometricaPendientes
            ).sum()
        } catch (e: Exception) {
            0 // En caso de error, retornar 0
        }
    }
}