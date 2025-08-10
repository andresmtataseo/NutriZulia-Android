package com.nutrizulia.domain.usecase.user

import com.nutrizulia.data.repository.user.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMaxAppointmentsPerDayUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<Int> {
        return userPreferencesRepository.maxAppointmentsPerDayFlow
    }
}