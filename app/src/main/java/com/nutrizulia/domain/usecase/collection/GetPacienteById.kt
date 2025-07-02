package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.PacienteRepository
import com.nutrizulia.domain.model.collection.Paciente
import javax.inject.Inject

class GetPacienteById @Inject constructor(
    private val pacienteRepository: PacienteRepository
) {
    suspend operator fun invoke(usuarioInstitucionId: Int, idPaciente: String) : Paciente? {
        return pacienteRepository.findById(usuarioInstitucionId, idPaciente)
    }
}