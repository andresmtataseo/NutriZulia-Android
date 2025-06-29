package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.usecase.collection.GetPacienteById
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccionesPacienteViewModel @Inject constructor(
    private val getPaciente: GetPacienteById
) : ViewModel() {

    private val _paciente = MutableLiveData<Paciente>()
    val paciente: MutableLiveData<Paciente> = _paciente

    private val _mensaje = MutableLiveData<String>()
    val mensaje: MutableLiveData<String> = _mensaje

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _salir = MutableLiveData<Boolean>()
    val salir: MutableLiveData<Boolean> = _salir

    fun obtenerPaciente(idPaciente: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            val paciente = getPaciente(idPaciente)
            if (paciente == null) {
                mensaje.postValue("No se encontró el paciente")
                salir.postValue(true)
                return@launch
            }
            _paciente.value = paciente
            isLoading.postValue(false)
        }
    }
}