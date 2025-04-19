package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.Entidad
import com.nutrizulia.domain.model.Municipio
import com.nutrizulia.domain.model.Paciente
import com.nutrizulia.domain.model.Parroquia
import com.nutrizulia.domain.usecase.GetUbicacionesUseCase
import com.nutrizulia.domain.usecase.InsertPacienteUseCase
import com.nutrizulia.util.CheckData.esCedulaValida
import com.nutrizulia.util.CheckData.esCorreoValido
import com.nutrizulia.util.CheckData.esFechaValida
import com.nutrizulia.util.CheckData.esNumeroTelefonoValido
import com.nutrizulia.util.FormatData.formatearCorreo
import com.nutrizulia.util.FormatData.formatearNombre
import com.nutrizulia.util.Utils.obtenerFechaActual
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrarPacienteViewModel @Inject constructor(
    private val insertPacienteUseCase: InsertPacienteUseCase,
    private val getUbicacionesUseCase: GetUbicacionesUseCase
) : ViewModel() {

    val mensaje = MutableLiveData<String>()
    val errores = MutableLiveData<Map<String, String>>()
    val salir = MutableLiveData<Boolean>()

    private val _entidades = MutableLiveData<List<Entidad>>()
    val entidades: LiveData<List<Entidad>> get() = _entidades

    private val _municipios = MutableLiveData<List<Municipio>>()
    val municipios: LiveData<List<Municipio>> get() = _municipios

    private val _parroquias = MutableLiveData<List<Parroquia>>()
    val parroquias: LiveData<List<Parroquia>> get() = _parroquias

    fun cargarEntidades() {
        viewModelScope.launch {
            val lista = getUbicacionesUseCase.getEntidades()
            _entidades.value = lista
        }
    }

    fun cargarMunicipios(codEntidad: String) {
        viewModelScope.launch {
            val lista = getUbicacionesUseCase.getMunicipios(codEntidad)
            _municipios.value = lista
        }
    }

    fun cargarParroquias(codEntidad: String, codMunicipio: String) {
        viewModelScope.launch {
            val lista = getUbicacionesUseCase.getParroquias(codEntidad, codMunicipio)
            _parroquias.value = lista
        }
    }

    fun registrarPaciente(paciente: Paciente) {
        val erroresMap = validarDatosPaciente(paciente)
        if (erroresMap.isNotEmpty()) {
            errores.value = erroresMap
            mensaje.value = "Error: Corrige los campos en rojo."
            return
        }

        formatearDatosPaciente(paciente)

        viewModelScope.launch {
            try {
                val result = insertPacienteUseCase(paciente)
                if (result > 0) {
                    mensaje.postValue("Paciente registrado correctamente.")
                    salir.postValue(true)
                } else {
                    mensaje.postValue("Error al registrar paciente.")
                    salir.postValue(false)
                }
            } catch (e: Exception) {
                mensaje.postValue("Error al registrar paciente.")
                salir.postValue(false)
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
        if (p.grupoSanguineo.isBlank()) {
            errores["grupoSanguineo"] = "El grupo sanguíneo es obligatorio."
        }
        if (p.ubicacionId == 0) {
            errores["direccion"] = "La dirección es obligatoria."
        }
        if (p.telefono.isNotEmpty()) {
            if (!esNumeroTelefonoValido(p.telefono)) {
                errores["telefono"] = "El teléfono no es válido."
            }
        }

        if (p.correo.isNotEmpty()) {
            if (!esCorreoValido(p.correo)) {
                errores["correo"] = "El correo no es válido."
            }
        }

        return errores
    }

    private fun formatearDatosPaciente(p: Paciente) {
        p.primerNombre = formatearNombre(p.primerNombre)
        p.segundoNombre = formatearNombre(p.segundoNombre)
        p.primerApellido = formatearNombre(p.primerApellido)
        p.segundoApellido = formatearNombre(p.segundoApellido)
        p.correo = formatearCorreo(p.correo)
        p.fechaIngreso = obtenerFechaActual()
    }
}
