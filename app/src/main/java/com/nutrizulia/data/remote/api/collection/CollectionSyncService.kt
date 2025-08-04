package com.nutrizulia.data.remote.api.collection

import com.nutrizulia.data.remote.dto.collection.ActividadDto
import com.nutrizulia.data.remote.dto.collection.ConsultaDto
import com.nutrizulia.data.remote.dto.collection.DetalleAntropometricoDto
import com.nutrizulia.data.remote.dto.collection.DetalleMetabolicoDto
import com.nutrizulia.data.remote.dto.collection.DetalleObstetriciaDto
import com.nutrizulia.data.remote.dto.collection.DetallePediatricoDto
import com.nutrizulia.data.remote.dto.collection.DetalleVitalDto
import com.nutrizulia.data.remote.dto.collection.DiagnosticoDto
import com.nutrizulia.data.remote.dto.collection.EvaluacionAntropometricaDto
import com.nutrizulia.data.remote.dto.collection.PacienteDto
import com.nutrizulia.data.remote.dto.collection.PacienteRepresentanteDto
import com.nutrizulia.data.remote.dto.collection.RepresentanteDto
import javax.inject.Inject

class CollectionSyncService @Inject constructor(
    private val api: ICollectionSyncService
) {
    suspend fun syncActividades(actividades: List<ActividadDto>) {
        api.syncActividades(actividades)
    }

    suspend fun syncConsultas(consultas: List<ConsultaDto>) {
        api.syncConsultas(consultas)
    }

    suspend fun sycnDetallesAntropometricos(detallesAntropometricos: List<DetalleAntropometricoDto>) {
        api.syncDetallesAntropometricos(detallesAntropometricos)
    }

    suspend fun syncDetallesMetabolicos(detallesMetabolicos: List<DetalleMetabolicoDto>) {
        api.syncDetallesMetabolicos(detallesMetabolicos)
    }

    suspend fun syncDetallesObstetricias(detallesObstetricias: List<DetalleObstetriciaDto>) {
        api.syncDetallesObstetricias(detallesObstetricias)
    }

    suspend fun syncDetallesPediatricos(detallesPediatricos: List<DetallePediatricoDto>) {
        api.syncDetallesPediatricos(detallesPediatricos)
    }

    suspend fun syncDetallesVitales(detallesVitales: List<DetalleVitalDto>) {
        api.syncDetallesVitales(detallesVitales)
    }

    suspend fun syncDiagnosticoDto(diagnosticoDto: List<DiagnosticoDto>) {
        api.syncDiagnosticoDto(diagnosticoDto)
    }

    suspend fun syncEvaluacionesAntropometricas(evaluacionesAntropometricas: List<EvaluacionAntropometricaDto>) {
        api.syncEvaluacionesAntropometricas(evaluacionesAntropometricas)
    }

    suspend fun syncPaciente(pacientes: List<PacienteDto>) {
        api.syncPacientes(pacientes)
    }

    suspend fun syncPacienteRepresentante(pacientesRepresentantes: List<PacienteRepresentanteDto>) {
        api.syncPacientesRepresentantes(pacientesRepresentantes)
    }

    suspend fun syncRepresentante(representantes: List<RepresentanteDto>) {
        api.syncRepresentantes(representantes)
    }

}