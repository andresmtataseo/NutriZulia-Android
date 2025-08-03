package com.nutrizulia.data.remote.api.collection

import com.nutrizulia.data.remote.dto.collection.PacienteRequestDto
import javax.inject.Inject

class PacienteService @Inject constructor(
    private val api: IPacienteService
) {
    suspend fun syncPaciente(pacientes: List<PacienteRequestDto>) {
        api.syncPacientes(pacientes)
    }
}