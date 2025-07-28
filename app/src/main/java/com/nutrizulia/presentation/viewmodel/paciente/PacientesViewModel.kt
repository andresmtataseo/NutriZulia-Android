package com.nutrizulia.presentation.viewmodel.paciente

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.usecase.collection.GetPacientes
import com.nutrizulia.domain.usecase.collection.GetPacientesByFiltro
import com.nutrizulia.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PacientesViewModel @Inject constructor(
    private val getPacientesByFiltro: GetPacientesByFiltro,
    private val getPacientes: GetPacientes,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _pacientes = MutableLiveData<List<Paciente>>()
    val pacientes: LiveData<List<Paciente>> get() = _pacientes

    private val _pacientesFiltrados = MutableLiveData<List<Paciente>>()
    val pacientesFiltrados: LiveData<List<Paciente>> get() = _pacientesFiltrados

    private val _filtro = MutableLiveData<String>()
    val filtro: LiveData<String> get() = _filtro

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    fun onCreate() {
        obtenerPacientes()
    }

    fun obtenerPacientes() {
        viewModelScope.launch {
            _isLoading.value = true

            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = "Error al buscar pacientes. No se ha seleccionado una institución."
            }

            val result = getPacientes(idUsuarioInstitucion.value ?: 0)
            if (result.isNotEmpty()) {
                _pacientes.value = result
            }
            _isLoading.value = false
        }
    }

    fun buscarPacientes(query: String) {
        viewModelScope.launch {
            _isLoading.value = true

            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = "Error al buscar pacientes. No se ha seleccionado una institución."
            }

            _filtro.value = query
            val result = getPacientesByFiltro(idUsuarioInstitucion.value ?: 0, filtro.value ?: "")
            if (result.isNotEmpty()) {
                _pacientesFiltrados.value = result
            } else {
                _mensaje.value = "No se encontraron pacientes."
            }
            _isLoading.value = false
            }
    }
}
