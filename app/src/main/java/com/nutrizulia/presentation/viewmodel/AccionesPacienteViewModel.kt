package com.nutrizulia.presentation.viewmodel

//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.nutrizulia.domain.model.Paciente
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class AccionesPacienteViewModel @Inject constructor(
//    private val getPaciente: GetPacienteByIdUseCase
//) : ViewModel() {
//
//    val pacientes = MutableLiveData<Paciente>()
//    val mensaje = MutableLiveData<String>()
//    val isLoading = MutableLiveData<Boolean>()
//
//    fun obtenerPaciente(idPaciente: Int) {
//        viewModelScope.launch {
//            isLoading.postValue(true)
//            val result = getPaciente(idPaciente)
//            pacientes.postValue(result)
//            isLoading.postValue(false)
//        }
//    }
//}