package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.ConsultaRepository
import com.nutrizulia.domain.model.ConsultaConPacienteYSignosVitales
import javax.inject.Inject

class GetConsultaConPacienteYSignosVitales @Inject constructor(
    private val repository: ConsultaRepository
){
    suspend operator fun invoke(consultaId: Int): ConsultaConPacienteYSignosVitales {
        return repository.getConsultaConPacienteYSignosVitalesById(consultaId) ?: throw Exception("Consulta no encontrada")
    }
}