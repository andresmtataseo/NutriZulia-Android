package com.nutrizulia.presentation.viewmodel

//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.nutrizulia.domain.model.Comunidad
//import com.nutrizulia.domain.model.Entidad
//import com.nutrizulia.domain.model.Municipio
//import com.nutrizulia.domain.model.Paciente
//import com.nutrizulia.domain.model.Parroquia
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class VerPacienteViewModel @Inject constructor(
//    private val getPacienteByIdUseCase: GetPacienteByIdUseCase,
//    private val getEntidad: GetEntidad,
//    private val getMunicipio: GetMunicipio,
//    private val getParroquia: GetParroquia,
//    private val getComunidad: GetComunidad,
//): ViewModel() {
//
//    private val _paciente = MutableLiveData<Paciente>()
//    val paciente: MutableLiveData<Paciente> = _paciente
//
//    private val _entidad = MutableLiveData<Entidad>()
//    val entidad: MutableLiveData<Entidad> = _entidad
//
//    private val _municipio = MutableLiveData<Municipio>()
//    val municipio: MutableLiveData<Municipio> = _municipio
//
//    private val _parroquia = MutableLiveData<Parroquia>()
//    val parroquia: MutableLiveData<Parroquia> = _parroquia
//
//    private val _comunidad = MutableLiveData<Comunidad>()
//    val comunidad: MutableLiveData<Comunidad> = _comunidad
//
//    private val _isLoading = MutableLiveData<Boolean>()
//    val isLoading: MutableLiveData<Boolean> = _isLoading
//
//    fun getPacienteById(idPaciente: Int) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            val paciente = getPacienteByIdUseCase(idPaciente)
//            _paciente.value = paciente
//            val entidad = getEntidad(paciente.codEntidad)
//            _entidad.value = entidad
//            val municipio = getMunicipio(entidad.codEntidad, paciente.codMunicipio)
//            _municipio.value = municipio
//            val parroquia = getParroquia(entidad.codEntidad, municipio.codMunicipio, paciente.codParroquia)
//            _parroquia.value = parroquia
//            val comunidad = getComunidad(entidad.codEntidad, municipio.codMunicipio, parroquia.codParroquia, paciente.idComunidad)
//            _comunidad.value = comunidad
//            _isLoading.value = false
//        }
//
//    }
//
//
//}