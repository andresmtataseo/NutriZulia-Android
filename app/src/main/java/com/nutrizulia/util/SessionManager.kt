package com.nutrizulia.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

@Singleton
class SessionManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val currentInstitutionKey = intPreferencesKey("current_usuario_institucion_id")

    val currentInstitutionIdFlow: Flow<Int?> = context.dataStore.data
        .map { preferences ->
            preferences[currentInstitutionKey]
        }

    suspend fun saveCurrentInstitutionId(id: Int) {
        context.dataStore.edit { preferences ->
            preferences[currentInstitutionKey] = id
        }
    }

    suspend fun clearCurrentInstitution() {
        context.dataStore.edit { preferences ->
            preferences.remove(currentInstitutionKey)
        }
    }
}