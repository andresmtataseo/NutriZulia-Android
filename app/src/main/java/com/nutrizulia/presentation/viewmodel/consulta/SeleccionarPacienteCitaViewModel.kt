package com.nutrizulia.presentation.viewmodel.consulta

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.collection.Consulta
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.usecase.collection.GetConsultaProgramadaByPacienteId
import com.nutrizulia.domain.usecase.collection.GetPacientes
import com.nutrizulia.domain.usecase.collection.GetPacientesByFiltro
import com.nutrizulia.util.Event
import com.nutrizulia.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeleccionarPacienteCitaViewModel @Inject constructor(
    private val getPacientes: GetPacientes,
    private val getPacientesByFiltro: GetPacientesByFiltro,
    private val getConsultaProgramadaByPacienteId: GetConsultaProgramadaByPacienteId,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _pacientes = MutableLiveData<List<Paciente>>()
    val pacientes: LiveData<List<Paciente>> get() = _pacientes

    private val _pacientesFiltrados = MutableLiveData<List<Paciente>>()
    val pacientesFiltrados: LiveData<List<Paciente>> get() = _pacientesFiltrados

    private val _consultaProgramada = MutableLiveData<Event<Consulta>>()
    val consultaProgramada: LiveData<Event<Consulta>> get() = _consultaProgramada

    private val _eventoNavegacion = MutableLiveData<Event<Pair<String, String?>>>()
    val eventoNavegacion: LiveData<Event<Pair<String, String?>>> get() = _eventoNavegacion

    private val _filtro = MutableLiveData<String>()
    val filtro: LiveData<String> get() = _filtro

    private val _mensaje = MutableLiveData<String?>()
    val mensaje: LiveData<String?> get() = _mensaje

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    fun onCreate() {
        obtenerPacientes()
    }

    fun obtenerPacientes() {
        viewModelScope.launch {

            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = "Error al buscar pacientes. No se ha seleccionado una institución."
            }

            _isLoading.value = true
            val result = getPacientes(idUsuarioInstitucion.value ?: 0)
            _pacientes.value = result
            _isLoading.value = false
        }
    }

    fun buscarPacientes(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _filtro.value = query
            if (query.isBlank()) {
                _pacientesFiltrados.value = emptyList()
                _isLoading.value = false
                return@launch
            }
            val result = getPacientesByFiltro(idUsuarioInstitucion.value ?: 0, filtro.value ?: "")
            if (result.isEmpty()) {
                _pacientesFiltrados.value = emptyList()
                _mensaje.value = "No se encontraron pacientes."
            } else {
                _pacientesFiltrados.value = result
            }
            _isLoading.value = false
        }
    }

    fun clearMensaje() {
        _mensaje.value = null
    }

    fun verificarCita(paciente: Paciente) {
        viewModelScope.launch {
            _isLoading.value = true
            val citaExistente = getConsultaProgramadaByPacienteId(paciente.id)

            if (citaExistente != null) {
                // Si SÍ hay cita, envuelve la consulta en un Event
                _consultaProgramada.value = Event(citaExistente)
            } else {
                // Si NO hay cita, envuelve los datos de navegación en un Event
                _eventoNavegacion.value = Event(Pair(paciente.id, null))
            }
            _isLoading.value = false
        }
    }
}