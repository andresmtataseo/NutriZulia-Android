package com.nutrizulia.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

private val Context.syncDataStore: DataStore<Preferences> by preferencesDataStore(name = "sync_prefs")

/**
 * Gestor de preferencias para la sincronización de datos.
 * Maneja el timestamp de la última sincronización exitosa usando DataStore.
 */
@Singleton
class SyncManager @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private val LAST_SUCCESSFUL_SYNC_KEY = stringPreferencesKey("last_successful_sync_timestamp")
        private val EPOCH_START = LocalDateTime.of(1970, 1, 1, 0, 0, 0)
        private val DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }

    /**
     * Flow que emite el timestamp de la última sincronización exitosa.
     * Si no hay sincronización previa, retorna el inicio de la época Unix.
     */
    val lastSuccessfulSyncTimestampFlow: Flow<LocalDateTime> = context.syncDataStore.data
        .map { preferences ->
            val timestampString = preferences[LAST_SUCCESSFUL_SYNC_KEY]
            timestampString?.let { 
                LocalDateTime.parse(it, DATE_FORMATTER) 
            } ?: EPOCH_START
        }

    /**
     * Obtiene el timestamp de la última sincronización exitosa.
     * Si no hay sincronización previa, retorna el inicio de la época Unix.
     */
    suspend fun getLastSuccessfulSyncTimestamp(): LocalDateTime {
        return lastSuccessfulSyncTimestampFlow.first()
    }

    /**
     * Guarda el timestamp de la última sincronización exitosa.
     */
    suspend fun saveLastSuccessfulSyncTimestamp(timestamp: LocalDateTime) {
        context.syncDataStore.edit { preferences ->
            preferences[LAST_SUCCESSFUL_SYNC_KEY] = timestamp.format(DATE_FORMATTER)
        }
    }

    /**
     * Limpia el timestamp de sincronización (útil para testing o reset).
     */
    suspend fun clearSyncTimestamp() {
        context.syncDataStore.edit { preferences ->
            preferences.remove(LAST_SUCCESSFUL_SYNC_KEY)
        }
    }
}