package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.local.view.PacienteConCita
import com.nutrizulia.domain.usecase.collection.GetPacientesConCitas
import com.nutrizulia.domain.usecase.collection.GetPacientesConCitasByFiltro
import com.nutrizulia.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsultasViewModel @Inject constructor(
    private val getPacientesConCitas: GetPacientesConCitas,
    private val getPacientesConCitasByFiltro: GetPacientesConCitasByFiltro,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _pacientesConCitas = MutableLiveData<List<PacienteConCita>>()
    val pacientesConCitas: LiveData<List<PacienteConCita>> get() = _pacientesConCitas

    private val _pacientesConCitasFiltrados = MutableLiveData<List<PacienteConCita>>()
    val pacientesConCitasFiltrados: LiveData<List<PacienteConCita>> get() = _pacientesConCitasFiltrados

    private val _filtro = MutableLiveData<String>()
    val filtro: LiveData<String> get() = _filtro

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    fun onCreate() {
        obtenerConsultas()
    }

    fun obtenerConsultas() {
        viewModelScope.launch {
            _isLoading.value = true

            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = "Error al buscar pacientes. No se ha seleccionado una institución."
            }

            val result = getPacientesConCitas(idUsuarioInstitucion.value ?: 0)
            if (result.isNotEmpty()) {
                _pacientesConCitas.value = result
            }
            _isLoading.value = false
        }
    }

    fun buscarConsultas(query: String) {
        viewModelScope.launch {
            _isLoading.value = true

            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = "Error al buscar pacientes. No se ha seleccionado una institución."
            }

            _filtro.value = query
            val result = getPacientesConCitasByFiltro(idUsuarioInstitucion.value ?: 0, filtro.value ?: "")
            if (result.isNotEmpty()) {
                _pacientesConCitasFiltrados.value = result
            }
            _isLoading.value = false
        }
    }

}