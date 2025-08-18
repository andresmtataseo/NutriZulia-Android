package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.PacienteRepresentanteRepository
import com.nutrizulia.domain.model.collection.PacienteRepresentado
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPacientesRepresentadosByRepresentanteId @Inject constructor(
    private val repository: PacienteRepresentanteRepository
) {
    suspend operator fun invoke(usuarioInstitucionId: Int, representanteId: String): List<PacienteRepresentado> {
        return repository.findAllPacientesRepresentadosByRepresentanteId(usuarioInstitucionId, representanteId)
    }

    fun asFlow(usuarioInstitucionId: Int, representanteId: String): Flow<List<PacienteRepresentado>> {
        return repository.findAllPacientesRepresentadosByRepresentanteIdFlow(usuarioInstitucionId, representanteId)
    }

    suspend fun withFilter(usuarioInstitucionId: Int, representanteId: String, query: String): List<PacienteRepresentado> {
        return repository.findAllPacientesRepresentadosByRepresentanteIdAndFilter(usuarioInstitucionId, representanteId, query)
    }
}