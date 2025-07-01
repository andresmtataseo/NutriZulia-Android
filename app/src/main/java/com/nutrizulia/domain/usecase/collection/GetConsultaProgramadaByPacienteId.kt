package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.ConsultaRepository
import com.nutrizulia.domain.model.collection.Consulta
import javax.inject.Inject

class GetConsultaProgramadaByPacienteId @Inject constructor(
    private val repository: ConsultaRepository
) {
    suspend operator fun invoke(idPaciente: String): Consulta? {
        return repository.findConsultaProgramadaByPacienteId(idPaciente)
    }
}