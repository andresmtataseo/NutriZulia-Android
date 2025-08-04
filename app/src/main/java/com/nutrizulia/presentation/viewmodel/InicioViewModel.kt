package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.usecase.collection.SyncCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InicioViewModel @Inject constructor(
    private val syncCollection: SyncCollection
) : ViewModel() {

    // Callbacks para manejar los diferentes estados de la sincronización
    var onSyncStart: (() -> Unit)? = null
    var onSyncSuccess: ((successCount: Int, totalOperations: Int, message: String) -> Unit)? = null
    var onSyncPartialSuccess: ((successCount: Int, totalOperations: Int, failedBatches: List<String>, message: String) -> Unit)? = null
    var onSyncError: ((message: String, details: String?) -> Unit)? = null

    fun sincronizarPacientes() {
        viewModelScope.launch {
            try {
                onSyncStart?.invoke()
                
                val results = syncCollection()
                val successCount = results.getSuccessCount()
                val totalOperations = results.getTotalOperations()
                
                if (results.hasErrors()) {
                    val failedBatches = mutableListOf<String>()
                    
                    if (results.batch1Result.hasErrors) failedBatches.add(results.batch1Result.batchName)
                    if (results.batch2Result.hasErrors) failedBatches.add(results.batch2Result.batchName)
                    if (results.batch3Result.hasErrors) failedBatches.add(results.batch3Result.batchName)
                    if (results.batch4Result.hasErrors) failedBatches.add(results.batch4Result.batchName)
                    if (results.batch5Result.hasErrors) failedBatches.add(results.batch5Result.batchName)
                    
                    if (successCount > 0) {
                        // Sincronización parcial
                        val message = "Sincronización parcial completada"
                        onSyncPartialSuccess?.invoke(successCount, totalOperations, failedBatches, message)
                    } else {
                        // Error total
                        val message = "Error en la sincronización"
                        val details = "Lotes fallidos: ${failedBatches.joinToString(", ")}"
                        onSyncError?.invoke(message, details)
                    }
                } else {
                    // Éxito total
                    val message = "Sincronización completada exitosamente"
                    onSyncSuccess?.invoke(successCount, totalOperations, message)
                }
            } catch (e: Exception) {
                onSyncError?.invoke("Error inesperado durante la sincronización", e.message)
            }
        }
    }
}