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
import com.nutrizulia.util.SessionManager
import kotlinx.coroutines.flow.firstOrNull
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
    private val evaluacionAntropometricaRepository: EvaluacionAntropometricaRepository,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(): Int {
        return try {
            // Obtener el ID de la institución actual
            val usuarioInstitucionId = sessionManager.currentInstitutionIdFlow.firstOrNull()
                ?: return 0 // Si no hay institución seleccionada, no hay registros pendientes
            
            val pacientesPendientes = pacienteRepository.findAllNotSynced(usuarioInstitucionId)
            val consultasPendientes = consultaRepository.findAllNotSynced(usuarioInstitucionId)
            val actividadesPendientes = actividadRepository.findAllNotSynced(usuarioInstitucionId)
            val representantesPendientes = representanteRepository.findAllNotSynced(usuarioInstitucionId)
            val pacienteRepresentantePendientes = pacienteRepresentanteRepository.findAllNotSynced(usuarioInstitucionId)
            val diagnosticosPendientes = diagnosticoRepository.findAllNotSynced(usuarioInstitucionId)
            val detalleAntropometricoPendientes = detalleAntropometricoRepository.findAllNotSynced(usuarioInstitucionId)
            val detalleMetabolicoPendientes = detalleMetabolicoRepository.findAllNotSynced(usuarioInstitucionId)
            val detalleObstetriciaPendientes = detalleObstetriciaRepository.findAllNotSynced(usuarioInstitucionId)
            val detallePediatricoPendientes = detallePediatricoRepository.findAllNotSynced(usuarioInstitucionId)
            val detalleVitalPendientes = detalleVitalRepository.findAllNotSynced(usuarioInstitucionId)
            val evaluacionAntropometricaPendientes = evaluacionAntropometricaRepository.findAllNotSynced(usuarioInstitucionId)
            
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