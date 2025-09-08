package com.nutrizulia.presentation.viewmodel.paciente

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.local.view.PacienteConCita
import com.nutrizulia.domain.usecase.collection.GetHistorialConsultasByPacienteId
import com.nutrizulia.domain.usecase.collection.GetHistorialConsultasByPacienteIdAndFiltro
import com.nutrizulia.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoriaPacienteViewModel @Inject constructor(
    private val getHistorialConsultasByPacienteId: GetHistorialConsultasByPacienteId,
    private val getHistorialConsultasByPacienteIdAndFiltro: GetHistorialConsultasByPacienteIdAndFiltro,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _consultasDetalladas = MutableLiveData<List<PacienteConCita>>()
    val consultasDetalladas: LiveData<List<PacienteConCita>> get() = _consultasDetalladas

    private val _consultasDetalladasFiltradas = MutableLiveData<List<PacienteConCita>>()
    val pacientesConCitasFiltrados: LiveData<List<PacienteConCita>> get() = _consultasDetalladasFiltradas

    private val _filtro = MutableLiveData<String>()
    val filtro: LiveData<String> get() = _filtro

    private val _mensaje = MutableLiveData<String?>()
    val mensaje: LiveData<String?> get() = _mensaje

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    fun onCreate(pacienteId: String) {
        obtenerConsultas(pacienteId)
    }

    fun clearMensaje() {
        _mensaje.value = null
    }

    fun obtenerConsultas(pacienteId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = getHistorialConsultasByPacienteId(pacienteId)
                _consultasDetalladas.value = result
                if (result.isEmpty()) {
                    _mensaje.value = "No se encontraron consultas completadas para este paciente."
                } else {
                    _mensaje.value = null
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al obtener las consultas: ${e.message}"
                _consultasDetalladas.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    fun buscarConsultas(pacienteId: String, filtro: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _filtro.value = filtro
            try {
                val result = getHistorialConsultasByPacienteIdAndFiltro(pacienteId, filtro)
                _consultasDetalladasFiltradas.value = result
                if (result.isEmpty() && filtro.isNotBlank()) {
                    _mensaje.value = "No se encontraron consultas que coincidan con la búsqueda."
                } else {
                    _mensaje.value = null
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al buscar consultas: ${e.message}"
                _consultasDetalladasFiltradas.value = emptyList()
            }
            _isLoading.value = false
        }
    }

}