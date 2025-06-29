package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.catalog.Estado
import com.nutrizulia.domain.model.catalog.Etnia
import com.nutrizulia.domain.model.catalog.Municipio
import com.nutrizulia.domain.model.catalog.Nacionalidad
import com.nutrizulia.domain.model.catalog.Parroquia
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.usecase.catalog.GetEstadoById
import com.nutrizulia.domain.usecase.catalog.GetEtniaById
import com.nutrizulia.domain.usecase.catalog.GetMunicipioById
import com.nutrizulia.domain.usecase.catalog.GetNacionalidadById
import com.nutrizulia.domain.usecase.catalog.GetParroquiaById
import com.nutrizulia.domain.usecase.collection.GetPacienteById
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerPacienteViewModel @Inject constructor(
    private val getPaciente: GetPacienteById,
    private val getEtnia: GetEtniaById,
    private val getNacionalidad: GetNacionalidadById,
    private val getEstado: GetEstadoById,
    private val getMunicipio: GetMunicipioById,
    private val getParroquia: GetParroquiaById
): ViewModel() {

    private val _paciente = MutableLiveData<Paciente>()
    val paciente: MutableLiveData<Paciente> = _paciente

    private val _etnia = MutableLiveData<Etnia>()
    val etnia: MutableLiveData<Etnia> = _etnia

    private val _nacionalidad = MutableLiveData<Nacionalidad>()
    val nacionalidad: MutableLiveData<Nacionalidad> = _nacionalidad

    private val _estado = MutableLiveData<Estado>()
    val estado: MutableLiveData<Estado> = _estado

    private val _municipio = MutableLiveData<Municipio>()
    val municipio: MutableLiveData<Municipio> = _municipio

    private val _parroquia = MutableLiveData<Parroquia>()
    val parroquia: MutableLiveData<Parroquia> = _parroquia

    private val _mensaje = MutableLiveData<String>()
    val mensaje: MutableLiveData<String> = _mensaje

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _salir = MutableLiveData<Boolean>()
    val salir: MutableLiveData<Boolean> = _salir

    fun obtenerPaciente(idPaciente: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            val paciente = getPaciente(idPaciente)
            if (paciente == null) {
                mensaje.postValue("No se encontró el paciente")
                salir.postValue(true)
                return@launch
            }
            _paciente.value = paciente
            _etnia.value = getEtnia(_paciente.value!!.etniaId)
            _nacionalidad.value = getNacionalidad(_paciente.value!!.nacionalidadId)
            _parroquia.value = getParroquia(_paciente.value!!.parroquiaId)
            _municipio.value = getMunicipio(_parroquia.value!!.municipioId)
            _estado.value  = getEstado(_municipio.value!!.estadoId)
            isLoading.postValue(false)
        }
    }

}