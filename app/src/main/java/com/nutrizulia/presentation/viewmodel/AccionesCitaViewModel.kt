package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.local.view.PacienteConCita
import com.nutrizulia.domain.usecase.collection.GetPacienteConCitaById
import com.nutrizulia.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccionesCitaViewModel @Inject constructor(
    private val getPacienteConCitaById: GetPacienteConCitaById,
    private val sessionManager: SessionManager
//    private val updateEstadoCitaUseCase: UpdateEstadoCitaUseCase,
) : ViewModel() {

    private val _pacienteConCita = MutableLiveData<PacienteConCita>()
    val pacienteConCita: LiveData<PacienteConCita> get() = _pacienteConCita

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _salir

    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> get() = _salir

    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    fun onCreate(id: String) {
        obtenerPacienteConCita(id)
    }

    fun obtenerPacienteConCita(consultaId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = "Error al buscar pacientes. No se ha seleccionado una institución."
            }

            val result = getPacienteConCitaById(idUsuarioInstitucion.value ?: 0, consultaId)
            if (result != null) {
                _pacienteConCita.value = result
            }
            _isLoading.value = false
        }
    }

    fun cancelarCita(idConsulta: String) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            val result = updateEstadoCitaUseCase(idCita, "CANCELADA")
//            if (result > 0) {
//                _salir.value = true
//                _mensaje.value = "Cita cancelada con éxito"
//            } else {
//                _mensaje.value = "Error: La cita no pudo ser cancelada"
//            }
//            _isLoading.value = false
//        }

    }
}