package com.nutrizulia.domain.usecase.user

import com.nutrizulia.data.repository.user.UserPreferencesRepository
import javax.inject.Inject

class SaveMaxAppointmentsPerDayUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(maxAppointments: Int) {
        require(maxAppointments > 0) { "La cantidad máxima de citas debe ser mayor a 0" }
        userPreferencesRepository.saveMaxAppointmentsPerDay(maxAppointments)
    }
}