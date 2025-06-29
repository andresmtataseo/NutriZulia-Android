package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.PacienteRepository
import com.nutrizulia.domain.model.collection.Paciente
import javax.inject.Inject

class GetPacienteByCedula @Inject constructor(
    private val pacienteRepository: PacienteRepository
) {
    suspend operator fun invoke(usuarioInstitucionId: Int, cedula: String): Paciente? {
        return pacienteRepository.findByCedula( usuarioInstitucionId, cedula)
    }
}