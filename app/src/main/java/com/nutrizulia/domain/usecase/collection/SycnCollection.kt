package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.remote.dto.collection.PacienteDto
import com.nutrizulia.data.repository.collection.PacienteRepository
import com.nutrizulia.domain.model.SyncResult
import javax.inject.Inject

class SycnCollection @Inject constructor(
    private val pacienteRepository: PacienteRepository
) {
    suspend operator fun invoke(): SyncResult<List<PacienteDto>> {
        return pacienteRepository.sincronizarPacientes()
    }
}