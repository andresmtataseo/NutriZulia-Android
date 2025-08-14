package com.nutrizulia.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.usecase.FullSyncCollections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel dedicado para la sincronización completa de datos del usuario.
 * Maneja el estado, progreso y resultados de la sincronización de todas las colecciones.
 */
@HiltViewModel
class DataSyncViewModel @Inject constructor(
    private val fullSyncCollections: FullSyncCollections
) : ViewModel() {

    private val _syncState = MutableLiveData<DataSyncState>()
    val syncState: LiveData<DataSyncState> get() = _syncState

    private val _syncProgress = MutableLiveData<SyncProgress>()
    val syncProgress: LiveData<SyncProgress> get() = _syncProgress

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    /**
     * Estados posibles de la sincronización de datos
     */
    sealed class DataSyncState {
        object Idle : DataSyncState()
        object InProgress : DataSyncState()
        data class Success(
            val totalRecords: Int,
            val tablesProcessed: Int,
            val summary: String
        ) : DataSyncState()
        data class Error(
            val message: String,
            val failedTables: List<String> = emptyList(),
            val details: String? = null
        ) : DataSyncState()
        data class PartialSuccess(
            val totalRecords: Int,
            val tablesSuccessful: Int,
            val tablesFailed: Int,
            val failedTables: List<String>,
            val summary: String
        ) : DataSyncState()
    }

    /**
     * Información de progreso de la sincronización
     */
    data class SyncProgress(
        val currentTable: String,
        val currentIndex: Int,
        val totalTables: Int,
        val progressPercentage: Int = (currentIndex * 100) / totalTables
    )

    /**
     * Inicia la sincronización completa de datos del usuario
     * @return true si la sincronización fue completamente exitosa, false en caso contrario
     */
    suspend fun syncUserData(): Boolean {
        if (_isLoading.value == true) {
            Log.w("DataSyncViewModel", "Sincronización ya en progreso, ignorando nueva solicitud")
            return false
        }

        return try {
            _isLoading.postValue(true)
            _syncState.postValue(DataSyncState.InProgress)
            
            Log.d("DataSyncViewModel", "Iniciando sincronización completa de datos del usuario")
            
            val result = fullSyncCollections.invoke { currentTable, currentIndex, totalTables ->
                val progress = SyncProgress(
                    currentTable = currentTable,
                    currentIndex = currentIndex,
                    totalTables = totalTables
                )
                _syncProgress.postValue(progress)
                Log.d("DataSyncViewModel", "Progreso: $currentTable ($currentIndex/$totalTables)")
            }
            
            when {
                result.overallSuccess -> {
                    Log.d("DataSyncViewModel", "Sincronización completa exitosa: ${result.totalRecordsRestored} registros")
                    _syncState.postValue(
                        DataSyncState.Success(
                            totalRecords = result.totalRecordsRestored,
                            tablesProcessed = result.tablesProcessed,
                            summary = result.summary
                        )
                    )
                    true
                }
                result.tablesSuccessful > 0 -> {
                    // Sincronización parcial - algunas tablas fallaron pero otras fueron exitosas
                    val failedTableNames = result.getTablesWithErrors().map { it.tableName }
                    Log.w("DataSyncViewModel", "Sincronización parcial: ${result.tablesSuccessful}/${result.tablesProcessed} tablas exitosas")
                    _syncState.postValue(
                        DataSyncState.PartialSuccess(
                            totalRecords = result.totalRecordsRestored,
                            tablesSuccessful = result.tablesSuccessful,
                            tablesFailed = result.tablesFailed,
                            failedTables = failedTableNames,
                            summary = result.summary
                        )
                    )
                    false // Consideramos como fallo si no todas las tablas fueron exitosas
                }
                else -> {
                    // Fallo completo
                    val failedTableNames = result.getTablesWithErrors().map { it.tableName }
                    val errorDetails = result.getTablesWithErrors().joinToString("; ") { 
                        "${it.tableName}: ${it.errorMessage}" 
                    }
                    Log.e("DataSyncViewModel", "Sincronización completa falló: $errorDetails")
                    _syncState.postValue(
                        DataSyncState.Error(
                            message = "Error en la sincronización de datos del usuario",
                            failedTables = failedTableNames,
                            details = errorDetails
                        )
                    )
                    false
                }
            }
        } catch (e: Exception) {
            Log.e("DataSyncViewModel", "Error crítico en sincronización de datos", e)
            _syncState.postValue(
                DataSyncState.Error(
                    message = "Error crítico al sincronizar datos: ${e.message}",
                    details = e.stackTraceToString()
                )
            )
            false
        } finally {
            _isLoading.postValue(false)
        }
    }

    /**
     * Función conveniente para uso desde corrutinas del ViewModel
     */
    fun syncUserDataAsync(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = syncUserData()
            onResult(success)
        }
    }

    /**
     * Resetea el estado del ViewModel
     */
    fun resetState() {
        _syncState.postValue(DataSyncState.Idle)
        _syncProgress.postValue(SyncProgress("Preparando...", 0, 1))
        _isLoading.postValue(false)
    }

    /**
     * Obtiene el mensaje de error actual si existe
     */
    fun getCurrentErrorMessage(): String? {
        return when (val state = _syncState.value) {
            is DataSyncState.Error -> state.message
            else -> null
        }
    }

    /**
     * Verifica si la última sincronización fue completamente exitosa
     */
    fun isLastSyncSuccessful(): Boolean {
        return _syncState.value is DataSyncState.Success
    }

    /**
     * Verifica si la última sincronización tuvo éxito parcial
     */
    fun isLastSyncPartiallySuccessful(): Boolean {
        return _syncState.value is DataSyncState.PartialSuccess
    }

    /**
     * Obtiene el progreso actual como porcentaje
     */
    fun getCurrentProgressPercentage(): Int {
        return _syncProgress.value?.progressPercentage ?: 0
    }

    /**
     * Obtiene información detallada del último resultado
     */
    fun getLastSyncSummary(): String? {
        return when (val state = _syncState.value) {
            is DataSyncState.Success -> state.summary
            is DataSyncState.PartialSuccess -> state.summary
            else -> null
        }
    }
}