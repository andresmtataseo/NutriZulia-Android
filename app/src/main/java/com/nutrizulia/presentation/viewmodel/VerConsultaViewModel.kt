package com.nutrizulia.presentation.viewmodel

//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.nutrizulia.domain.model.ConsultaConPacienteYSignosVitales
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class VerConsultaViewModel @Inject constructor(
//    private val getConsultaConPacienteYSignosVitales: GetConsultaConPacienteYSignosVitales
//): ViewModel() {
//
//    private val _consultaConPacienteYSignosVitales = MutableLiveData<ConsultaConPacienteYSignosVitales>()
//    val consultaConPacienteYSignosVitales: LiveData<ConsultaConPacienteYSignosVitales> get() = _consultaConPacienteYSignosVitales
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
//    fun obtenerPaciente(idConsulta: Int) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val result = getConsultaConPacienteYSignosVitales(idConsulta)
//                if (result != null) {
//                    _consultaConPacienteYSignosVitales.value = result
//                    _salir.value = false
//                } else {
//                    _mensaje.value = "Error: Cita no encontrada"
//                    _salir.value = true
//                }
//                _isLoading.value = false
//            } catch (e: Exception) {
//                _mensaje.value = "Error: ${e.message}"
//                _salir.value = true
//                _isLoading.value = false
//
//            }
//        }
//    }
//
//}