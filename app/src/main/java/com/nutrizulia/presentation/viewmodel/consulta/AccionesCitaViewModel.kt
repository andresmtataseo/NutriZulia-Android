package com.nutrizulia.presentation.viewmodel.consulta

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.view.PacienteConCita
import com.nutrizulia.domain.usecase.collection.GetPacienteConCitaById
import com.nutrizulia.domain.usecase.collection.SaveConsultaEstadoById
import com.nutrizulia.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccionesCitaViewModel @Inject constructor(
    private val getPacienteConCitaById: GetPacienteConCitaById,
    private val sessionManager: SessionManager,
    private val saveConsultaEstadoById: SaveConsultaEstadoById
) : ViewModel() {

    private val _pacienteConCita = MutableLiveData<PacienteConCita>()
    val pacienteConCita: LiveData<PacienteConCita> get() = _pacienteConCita
    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> get() = _salir

    fun onCreate(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val pacienteJob = launch { obtenerPacienteConCita(id) }
                pacienteJob.join()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun obtenerPacienteConCita(consultaId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = "No se ha seleccionado una institución."
                _isLoading.value = false
                _salir.value = true
                return@launch
            }

            val result = getPacienteConCitaById(idUsuarioInstitucion.value ?: 0, consultaId)
            if (result != null) {
                _pacienteConCita.value = result
            } else {
                _mensaje.value = "No se encontraron datos."
                _isLoading.value = false
                _salir.value = true
                return@launch
            }
            _isLoading.value = false
        }
    }

    fun cancelarCita(idConsulta: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                saveConsultaEstadoById(idConsulta, Estado.CANCELADA)
                _mensaje.value = "Cita cancelada con éxito."
                _salir.value = true
            } catch (e: Exception) {
                _mensaje.value = "Error al cancelar la cita."
            } finally {
                _isLoading.value = false
            }

        }

    }
}