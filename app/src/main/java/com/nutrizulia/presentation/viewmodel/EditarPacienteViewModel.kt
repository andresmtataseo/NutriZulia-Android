package com.nutrizulia.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.exception.DomainException
import com.nutrizulia.domain.model.Comunidad
import com.nutrizulia.domain.model.Entidad
import com.nutrizulia.domain.model.Municipio
import com.nutrizulia.domain.model.Paciente
import com.nutrizulia.domain.model.Parroquia
import com.nutrizulia.domain.usecase.GetComunidad
import com.nutrizulia.domain.usecase.GetComunidades
import com.nutrizulia.domain.usecase.GetEntidad
import com.nutrizulia.domain.usecase.GetEntidades
import com.nutrizulia.domain.usecase.GetMunicipio
import com.nutrizulia.domain.usecase.GetMunicipios
import com.nutrizulia.domain.usecase.GetPacienteByIdUseCase
import com.nutrizulia.domain.usecase.GetParroquia
import com.nutrizulia.domain.usecase.GetParroquias
import com.nutrizulia.domain.usecase.UpdatePaciente
import com.nutrizulia.util.CheckData.esCedulaValida
import com.nutrizulia.util.CheckData.esCorreoValido
import com.nutrizulia.util.CheckData.esFechaValida
import com.nutrizulia.util.CheckData.esNumeroTelefonoValido
import com.nutrizulia.util.FormatData.formatearCedula
import com.nutrizulia.util.FormatData.formatearTelefono
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditarPacienteViewModel @Inject constructor(
    private val getPacienteByIdUseCase: GetPacienteByIdUseCase,
    private val updatePaciente: UpdatePaciente,
    private val getEntidad: GetEntidad,
    private val getMunicipio: GetMunicipio,
    private val getParroquia: GetParroquia,
    private val getComunidad: GetComunidad,
    private val getEntidades: GetEntidades,
    private val getMunicipios: GetMunicipios,
    private val getParroquias: GetParroquias,
    private val getComunidades: GetComunidades
): ViewModel() {

    private val _paciente = MutableLiveData<Paciente>()
    val paciente: LiveData<Paciente>  get() = _paciente

    private val _entidad = MutableLiveData<Entidad>()
    val entidad: LiveData<Entidad> get() = _entidad

    private val _municipio = MutableLiveData<Municipio>()
    val municipio: LiveData<Municipio> get() = _municipio

    private val _parroquia = MutableLiveData<Parroquia>()
    val parroquia: LiveData<Parroquia> get() = _parroquia

    private val _comunidad = MutableLiveData<Comunidad>()
    val comunidad: LiveData<Comunidad> get() = _comunidad

    private val _entidades = MutableLiveData<List<Entidad>>()
    val entidades: LiveData<List<Entidad>> get() = _entidades

    private val _municipios = MutableLiveData<List<Municipio>>()
    val municipios: LiveData<List<Municipio>> get() = _municipios

    private val _parroquias = MutableLiveData<List<Parroquia>>()
    val parroquias: LiveData<List<Parroquia>> get() = _parroquias

    private val _comunidades = MutableLiveData<List<Comunidad>>()
    val comunidades: LiveData<List<Comunidad>> get() = _comunidades

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje

    private val _errores = MutableLiveData<Map<String, String>>()
    val errores: LiveData<Map<String, String>> get() = _errores

    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> get() = _salir


    fun getPacienteById(idPaciente: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val paciente = getPacienteByIdUseCase(idPaciente)
            _paciente.value = paciente
            val entidad = getEntidad(paciente.codEntidad)
            _entidad.value = entidad
            val municipio = getMunicipio(entidad.codEntidad, paciente.codMunicipio)
            _municipio.value = municipio
            val parroquia = getParroquia(entidad.codEntidad, municipio.codMunicipio, paciente.codParroquia)
            _parroquia.value = parroquia
            val comunidad = getComunidad(entidad.codEntidad, municipio.codMunicipio, parroquia.codParroquia, paciente.idComunidad)
            _comunidad.value = comunidad
            _isLoading.value = false
        }

    }

    fun cargarEntidades() {
        viewModelScope.launch {
            val lista = getEntidades()
            _entidades.value = lista
        }
    }

    fun cargarMunicipios(codEntidad: String) {
        viewModelScope.launch {
            val lista = getMunicipios(codEntidad)
            _municipios.value = lista
        }
    }

    fun cargarParroquias(codEntidad: String, codMunicipio: String) {
        viewModelScope.launch {
            val lista = getParroquias(codEntidad, codMunicipio)
            _parroquias.value = lista
        }
    }

    fun cargarComunidades(codEntidad: String, codMunicipio: String, codParroquia: String) {
        viewModelScope.launch {
            val lista = getComunidades(codEntidad, codMunicipio, codParroquia)
            _comunidades.value = lista
        }
    }

    fun actualizarPaciente(paciente: Paciente) {
        val erroresMap = validarDatosPaciente(paciente)
        if (erroresMap.isNotEmpty()) {
            _errores.value = erroresMap
            _mensaje.value = "Error: Corrige los campos en rojo."
            return
        }

        formatearDatosPaciente(paciente)

        viewModelScope.launch {
            try {
                val result = updatePaciente(paciente)
                if (result > 0) {
                    _mensaje.postValue("Paciente actualizado correctamente.")
                    _salir.postValue(true)
                } else {
                    _mensaje.postValue("Error desconocido al actualizar el paciente (código: $result).")
                    _salir.postValue(false)
                }
            } catch (e: DomainException) {
                _mensaje.postValue(e.message)
                _salir.postValue(false)
            } catch (e: Exception) {
                _mensaje.postValue("Ocurrió un error inesperado al registrar paciente: ${e.message}")
                _salir.postValue(false)
                Log.e("RegistrarPacienteViewModel", "Error al registrar paciente", e)
            }

        }
    }

    private fun validarDatosPaciente(p: Paciente): Map<String, String> {
        val errores = mutableMapOf<String, String>()

        if (p.cedula.isBlank()) {
            errores["cedula"] = "La cédula es obligatoria."
        } else if (!esCedulaValida(p.cedula)) {
            errores["cedula"] = "La cédula no es válida."
        }
        if (p.primerNombre.isBlank()) {
            errores["primerNombre"] = "El primer nombre es obligatorio."
        }
        if (p.primerApellido.isBlank()) {
            errores["primerApellido"] = "El primer apellido es obligatorio."
        }
        if (p.segundoApellido.isBlank()) {
            errores["segundoApellido"] = "El segundo apellido es obligatorio."
        }
        if (p.fechaNacimiento.isBlank()) {
            errores["fechaNacimiento"] = "La fecha de nacimiento es obligatoria."
        } else if (!esFechaValida(p.fechaNacimiento)) {
            errores["fechaNacimiento"] = "El formato de la fecha es inválido. Ejemplo: DD-MM-YYYY."
        }

        if (p.genero.isBlank()) {
            errores["genero"] = "El género es obligatorio."
        }
        if (p.etnia.isBlank()) {
            errores["etnia"] = "La etnia es obligatoria."
        }
        if (p.nacionalidad.isBlank()) {
            errores["nacionalidad"] = "La nacionalidad es obligatoria."
        }
        if (p.codEntidad.isBlank()) {
            errores["estado"] = "El estado es obligatorio."
        }
        if (p.codMunicipio.isBlank()) {
            errores["municipio"] = "El municipio es obligatorio."
        }
        if (p.codParroquia.isBlank()) {
            errores["parroquia"] = "La parroquia es obligatoria."
        }
        if (p.idComunidad.isBlank()) {
            errores["comunidad"] = "La comunidad es obligatoria."
        }
        if (p.telefono?.isNotEmpty() == true) {
            if (!esNumeroTelefonoValido(p.telefono)) {
                errores["telefono"] = "El teléfono no es válido."
            }
        }
        if (p.correo?.isNotEmpty() == true) {
            if (!esCorreoValido(p.correo)) {
                errores["correo"] = "El correo no es válido."
            }
        }

        return errores
    }

    private fun formatearDatosPaciente(p: Paciente) {
        p.cedula = formatearCedula(p.cedula)
        p.primerNombre = p.primerNombre.uppercase().trim()
        p.segundoNombre = p.segundoNombre?.uppercase()?.trim()
        p.primerApellido = p.primerApellido.uppercase().trim()
        p.segundoApellido = p.segundoApellido.uppercase().trim()
        p.genero = p.genero.uppercase().trim()
        p.etnia = p.etnia.uppercase().trim()
        p.nacionalidad = p.nacionalidad.uppercase().trim()
        p.telefono = formatearTelefono(p.telefono)
        p.correo = p.correo?.lowercase()?.trim()
    }

}