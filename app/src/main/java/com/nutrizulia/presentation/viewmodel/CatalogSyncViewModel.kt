package com.nutrizulia.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.usecase.catalog.SyncCatalog
import com.nutrizulia.domain.usecase.catalog.SyncCatalogsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel dedicado exclusivamente para la sincronización de catálogos.
 * Maneja el estado y progreso de la sincronización de catálogos de forma independiente.
 */
@HiltViewModel
class CatalogSyncViewModel @Inject constructor(
    private val syncCatalog: SyncCatalog
) : ViewModel() {

    private val _syncState = MutableLiveData<CatalogSyncState>()
    val syncState: LiveData<CatalogSyncState> get() = _syncState

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    /**
     * Estados posibles de la sincronización de catálogos
     */
    sealed class CatalogSyncState {
        object Idle : CatalogSyncState()
        object InProgress : CatalogSyncState()
        object Success : CatalogSyncState()
        data class Error(val message: String, val details: String? = null) : CatalogSyncState()
    }

    /**
     * Inicia la sincronización de catálogos
     * @return true si la sincronización fue exitosa, false en caso contrario
     */
    suspend fun syncCatalogs(): Boolean {
        if (_isLoading.value == true) {
            Log.w("CatalogSyncViewModel", "Sincronización ya en progreso, ignorando nueva solicitud")
            return false
        }

        return try {
            _isLoading.postValue(true)
            _syncState.postValue(CatalogSyncState.InProgress)
            
            Log.d("CatalogSyncViewModel", "Iniciando sincronización de catálogos")
            
            when (val result = syncCatalog()) {
                is SyncCatalogsResult.Success -> {
                    Log.d("CatalogSyncViewModel", "Sincronización de catálogos completada exitosamente")
                    _syncState.postValue(CatalogSyncState.Success)
                    true
                }
                is SyncCatalogsResult.Failure -> {
                    Log.e("CatalogSyncViewModel", "Error en sincronización de catálogos: ${result.message}")
                    val detailsMessage = result.details.joinToString(", ") { "${it.tableName}: ${it.message}" }
                    _syncState.postValue(
                        CatalogSyncState.Error(
                            message = result.message,
                            details = detailsMessage
                        )
                    )
                    false
                }
            }
        } catch (e: Exception) {
            Log.e("CatalogSyncViewModel", "Error crítico en sincronización de catálogos", e)
            _syncState.postValue(
                CatalogSyncState.Error(
                    message = "Error crítico al sincronizar catálogos: ${e.message}",
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
    fun syncCatalogsAsync(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = syncCatalogs()
            onResult(success)
        }
    }

    /**
     * Resetea el estado del ViewModel
     */
    fun resetState() {
        _syncState.postValue(CatalogSyncState.Idle)
        _isLoading.postValue(false)
    }

    /**
     * Obtiene el mensaje de error actual si existe
     */
    fun getCurrentErrorMessage(): String? {
        return when (val state = _syncState.value) {
            is CatalogSyncState.Error -> state.message
            else -> null
        }
    }

    /**
     * Verifica si la sincronización fue exitosa
     */
    fun isLastSyncSuccessful(): Boolean {
        return _syncState.value is CatalogSyncState.Success
    }
}