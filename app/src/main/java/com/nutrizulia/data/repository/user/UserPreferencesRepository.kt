package com.nutrizulia.data.repository.user

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private val MAX_APPOINTMENTS_PER_DAY_KEY = intPreferencesKey("max_appointments_per_day")
        const val DEFAULT_MAX_APPOINTMENTS_PER_DAY = 8
    }

    val maxAppointmentsPerDayFlow: Flow<Int> = context.userPreferencesDataStore.data
        .map { preferences ->
            preferences[MAX_APPOINTMENTS_PER_DAY_KEY] ?: DEFAULT_MAX_APPOINTMENTS_PER_DAY
        }

    suspend fun saveMaxAppointmentsPerDay(maxAppointments: Int) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[MAX_APPOINTMENTS_PER_DAY_KEY] = maxAppointments
        }
    }

    suspend fun getMaxAppointmentsPerDay(): Int {
        return maxAppointmentsPerDayFlow.first()
    }
}