package com.nutrizulia.presentation.viewmodel

//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.nutrizulia.domain.model.CitaConPaciente
//import com.nutrizulia.domain.model.Consulta
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class AccionesConsultaViewModel @Inject constructor(
//    private val getCitaConPaciente: GetCitaConPacienteUseCase,
//    private val getConsultaByCitaId: GetConsultaByCita
//) : ViewModel() {
//
//    private var _citaConPaciente = MutableLiveData<CitaConPaciente>()
//    val citaConPaciente: LiveData<CitaConPaciente> get() = _citaConPaciente
//
//    private var _consulta = MutableLiveData<Consulta>()
//    val consulta: LiveData<Consulta> get() = _consulta
//
//    private var _mensaje = MutableLiveData<String>()
//    val mensaje: LiveData<String> get() = _mensaje
//
//    private var _salir = MutableLiveData<Boolean>()
//    val salir: LiveData<Boolean> get() = _salir
//
//    private var _isLoading = MutableLiveData<Boolean>()
//    val isLoading: LiveData<Boolean> get() = _isLoading
//
//    fun obtenerPaciente(idCita: Int) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            val result = getCitaConPaciente(idCita)
//            if (result != null) {
//                _citaConPaciente.value = result
//            } else {
//                _mensaje.value = "Error: Cita no encontrada"
//            }
//            _isLoading.value = false
//        }
//    }
//
//    fun obtenerConsulta(idCita: Int) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            val result = getConsultaByCitaId(idCita)
//            if (result != null) {
//                _consulta.value = result
//            } else {
//                _mensaje.value = "Error: Consulta no encontrada"
//            }
//            _isLoading.value = false
//        }
//    }
//
//
//    fun borrarConsulta(idCita: Int) {
////        viewModelScope.launch {
////            _isLoading.value = true
////            val result = getCitaConPacienteConsulta(idCita, "CANCELADA")
////            if (result > 0) {
////                _salir.value = true
////                _mensaje.value = "Cita cancelada con éxito"
////            } else {
////                _mensaje.value = "Error: La cita no pudo ser cancelada"
////            }
////            _isLoading.value = false
////        }
//
//    }
//
//}