package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.PacienteRepository
import com.nutrizulia.domain.model.Paciente
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPacientesByFiltro @Inject constructor(
    private val repository: PacienteRepository
) {

    operator fun invoke(filtro: String): Flow<List<Paciente>> {
        return repository.getPacientesByFiltro(filtro)
    }

}