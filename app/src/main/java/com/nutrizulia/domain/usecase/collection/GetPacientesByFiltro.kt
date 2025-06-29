package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.PacienteRepository
import com.nutrizulia.domain.model.collection.Paciente
import javax.inject.Inject

class GetPacientesByFiltro @Inject constructor(private val repository: PacienteRepository) {

    suspend operator fun invoke( usuarioInstitucionId: Int, query: String) : List<Paciente> {
        return repository.findAllByFiltro( usuarioInstitucionId, query)
    }

}