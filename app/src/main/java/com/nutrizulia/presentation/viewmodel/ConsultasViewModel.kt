package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.Cita
import com.nutrizulia.domain.usecase.GetCitasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsultasViewModel @Inject constructor(
    private val getCitasUseCase: GetCitasUseCase
): ViewModel() {

    private val _citas = MutableLiveData<List<Cita>>()
    val citas: LiveData<List<Cita>> get() = _citas

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun onCreate() {
        obtenerCitas()
    }

    fun obtenerCitas() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = getCitasUseCase()
            if (result.isNotEmpty()) {
                _citas.value = result
            } else {
                _mensaje.value = "No se encontraron citas."
            }
            _isLoading.value = false
        }
    }

}