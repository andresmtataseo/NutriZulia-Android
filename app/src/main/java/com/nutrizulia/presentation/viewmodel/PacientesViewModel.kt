package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.Paciente
import com.nutrizulia.domain.usecase.GetPacientesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PacientesViewModel @Inject constructor(
    private val getPacientesUseCase: GetPacientesUseCase
) : ViewModel() {

    val pacientes = MutableLiveData<List<Paciente>?>()
    val mensaje = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()

    fun onCreate() {
        obtenerPacientes()
    }

    fun obtenerPacientes() {
        viewModelScope.launch {
            isLoading.postValue(true)
            val result = getPacientesUseCase()
            if (!result.isNullOrEmpty()) {
                pacientes.postValue(result)
            } else {
                mensaje.postValue("No se encontraron pacientes.")
            }
            isLoading.postValue(false)
        }
    }

}