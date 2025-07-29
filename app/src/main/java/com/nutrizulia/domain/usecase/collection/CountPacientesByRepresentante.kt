package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.PacienteRepresentanteRepository
import javax.inject.Inject

class CountPacientesByRepresentante @Inject constructor(
    private val repository: PacienteRepresentanteRepository
){
    suspend operator fun invoke(usuarioInstitucionId: Int, representanteId: String): Int {
        return repository.countPacienteIdByUsuarioInstitucionIdAndRepresentanteId(usuarioInstitucionId, representanteId)
    }
}