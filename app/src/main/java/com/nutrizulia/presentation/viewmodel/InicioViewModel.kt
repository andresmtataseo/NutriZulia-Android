package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.SyncResult
import com.nutrizulia.domain.usecase.collection.SyncCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.ConnectException
import java.net.UnknownHostException
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
                    var networkError = false
                    var connectivityError = false
                    
                    // Verificar tipos de errores en cada lote
                    listOf(results.batch1Result, results.batch2Result, results.batch3Result, 
                           results.batch4Result, results.batch5Result).forEach { batch ->
                        if (batch.hasErrors) {
                            failedBatches.add(batch.batchName)
                            
                            // Verificar si hay errores de conectividad
                             batch.results.forEach { result ->
                                 when (result) {
                                     is SyncResult.UnknownError -> {
                                         when (result.exception) {
                                             is SocketTimeoutException, 
                                             is ConnectException, 
                                             is UnknownHostException -> {
                                                 connectivityError = true
                                             }
                                         }
                                     }
                                     is SyncResult.NetworkError -> {
                                         networkError = true
                                     }
                                     is SyncResult.BusinessError -> {
                                         // Error de negocio, no es problema de conectividad
                                     }
                                     is SyncResult.Success -> {
                                         // Éxito, no hay error
                                     }
                                 }
                             }
                        }
                    }
                    
                    if (successCount > 0) {
                        // Sincronización parcial
                        val message = if (connectivityError) {
                            "Sincronización parcial - Problemas de conectividad"
                        } else if (networkError) {
                            "Sincronización parcial - Errores de red"
                        } else {
                            "Sincronización parcial completada"
                        }
                        onSyncPartialSuccess?.invoke(successCount, totalOperations, failedBatches, message)
                    } else {
                        // Error total
                        val (message, details) = when {
                            connectivityError -> {
                                "Error de conectividad" to "No se puede conectar al servidor. Verifique su conexión a internet y que el servidor esté disponible."
                            }
                            networkError -> {
                                "Error de red" to "Problemas de comunicación con el servidor. Intente nuevamente."
                            }
                            else -> {
                                "Error en la sincronización" to "Lotes fallidos: ${failedBatches.joinToString(", ")}"
                            }
                        }
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