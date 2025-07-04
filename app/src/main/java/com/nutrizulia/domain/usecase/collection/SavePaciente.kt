package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.PacienteRepository
import com.nutrizulia.domain.model.collection.Paciente
import javax.inject.Inject

class SavePaciente @Inject constructor(
    private val repository: PacienteRepository
) {

    suspend operator fun invoke(paciente: Paciente): Long {
        return repository.upsert(paciente)
    }

}