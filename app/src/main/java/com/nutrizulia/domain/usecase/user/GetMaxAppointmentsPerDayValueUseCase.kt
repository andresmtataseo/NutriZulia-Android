package com.nutrizulia.domain.usecase.user

import com.nutrizulia.data.repository.user.UserPreferencesRepository
import javax.inject.Inject

class GetMaxAppointmentsPerDayValueUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Int {
        return userPreferencesRepository.getMaxAppointmentsPerDay()
    }
}