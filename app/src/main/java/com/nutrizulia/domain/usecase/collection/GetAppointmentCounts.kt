package com.nutrizulia.domain.usecase.collection

import com.nutrizulia.data.repository.collection.ConsultaRepository
import java.time.LocalDate
import javax.inject.Inject

class GetAppointmentCounts @Inject constructor(
    private val consultaRepository: ConsultaRepository
) {
    suspend operator fun invoke(usuarioInstitucionId: Int): Map<LocalDate, Int> {
        return consultaRepository.getAppointmentCountsByDay(usuarioInstitucionId)
    }
}