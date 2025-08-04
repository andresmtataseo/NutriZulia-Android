package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.remote.dto.collection.PacienteDto
import com.nutrizulia.domain.model.onBusinessError
import com.nutrizulia.domain.model.onConflictError
import com.nutrizulia.domain.model.onNetworkError
import com.nutrizulia.domain.model.onSuccess
import com.nutrizulia.domain.model.onUnknownError
import com.nutrizulia.domain.usecase.collection.SycnCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InicioViewModel @Inject constructor(
    private val sycnCollection: SycnCollection
) : ViewModel() {

    // Callbacks para manejar los diferentes estados de la sincronización
    var onSyncStart: (() -> Unit)? = null
    var onSyncSuccess: ((data: List<PacienteDto>, message: String) -> Unit)? = null
    var onSyncBusinessError: ((status: Int, message: String, errors: Map<String, String>?) -> Unit)? = null
    var onSyncConflictError: ((message: String, errors: Map<String, String>?) -> Unit)? = null
    var onSyncNetworkError: ((code: Int, message: String) -> Unit)? = null
    var onSyncUnknownError: ((exception: Throwable) -> Unit)? = null

    fun sincronizarPacientes(): Unit {
        viewModelScope.launch {
            onSyncStart?.invoke()
            
            sycnCollection()
                .onSuccess { data, message -> onSyncSuccess?.invoke(data, message) }
                .onConflictError { message, errors -> onSyncConflictError?.invoke(message, errors) }
                .onBusinessError { status, message, errors -> onSyncBusinessError?.invoke(status, message, errors) }
                .onNetworkError { code, message -> onSyncNetworkError?.invoke(code, message) }
                .onUnknownError { exception -> onSyncUnknownError?.invoke(exception) }
        }
    }
}