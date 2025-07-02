package com.nutrizulia.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.exception.DomainException
import com.nutrizulia.domain.model.catalog.Estado
import com.nutrizulia.domain.model.catalog.Etnia
import com.nutrizulia.domain.model.catalog.Municipio
import com.nutrizulia.domain.model.catalog.Nacionalidad
import com.nutrizulia.domain.model.catalog.Parroquia
import com.nutrizulia.domain.model.collection.Consulta
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.usecase.catalog.GetEstadoById
import com.nutrizulia.domain.usecase.catalog.GetEstados
import com.nutrizulia.domain.usecase.catalog.GetEtniaById
import com.nutrizulia.domain.usecase.catalog.GetEtnias
import com.nutrizulia.domain.usecase.catalog.GetMunicipioById
import com.nutrizulia.domain.usecase.catalog.GetMunicipios
import com.nutrizulia.domain.usecase.catalog.GetNacionalidadById
import com.nutrizulia.domain.usecase.catalog.GetNacionalidades
import com.nutrizulia.domain.usecase.catalog.GetParroquiaById
import com.nutrizulia.domain.usecase.catalog.GetParroquias
import com.nutrizulia.domain.usecase.collection.GetPacienteByCedula
import com.nutrizulia.domain.usecase.collection.GetPacienteById
import com.nutrizulia.domain.usecase.collection.SavePaciente
import com.nutrizulia.util.CheckData.esCedulaValida
import com.nutrizulia.util.CheckData.esCorreoValido
import com.nutrizulia.util.CheckData.esNumeroTelefonoValido
import com.nutrizulia.util.FormatData.formatearCedula
import com.nutrizulia.util.FormatData.formatearTelefono
import com.nutrizulia.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RegistrarPacienteViewModel @Inject constructor(
    private val savePaciente: SavePaciente,
    private val getPacienteByCedula: GetPacienteByCedula,
    private val getPacienteById: GetPacienteById,
    private val getEtnia: GetEtniaById,
    private val getNacionalidad: GetNacionalidadById,
    private val getEstado: GetEstadoById,
    private val getMunicipio: GetMunicipioById,
    private val getParroquia: GetParroquiaById,
    private val getEtnias: GetEtnias,
    private val getNacionalidades: GetNacionalidades,
    private val getEstados: GetEstados,
    private val getMunicipios: GetMunicipios,
    private val getParroquias: GetParroquias,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _paciente = MutableLiveData<Paciente>()
    val paciente: LiveData<Paciente> = _paciente
    private val _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    private val _etnia = MutableLiveData<Etnia>()
    val etnia: LiveData<Etnia> get() = _etnia
    private val _nacionalidad = MutableLiveData<Nacionalidad>()
    val nacionalidad: LiveData<Nacionalidad> get() = _nacionalidad
    private val _estado = MutableLiveData<Estado>()
    val estado: LiveData<Estado> get() = _estado
    private val _municipio = MutableLiveData<Municipio>()
    val municipio: LiveData<Municipio> get() = _municipio
    private val _parroquia = MutableLiveData<Parroquia>()
    val parroquia: LiveData<Parroquia> get() = _parroquia

    private val _etnias = MutableLiveData<List<Etnia>>()
    val etnias: LiveData<List<Etnia>> get() = _etnias
    private val _nacionalidades = MutableLiveData<List<Nacionalidad>>()
    val nacionalidades: LiveData<List<Nacionalidad>> get() = _nacionalidades
    private val _estados = MutableLiveData<List<Estado>>()
    val estados: LiveData<List<Estado>> get() = _estados
    private val _municipios = MutableLiveData<List<Municipio>>()
    val municipios: LiveData<List<Municipio>> get() = _municipios
    private val _parroquias = MutableLiveData<List<Parroquia>>()
    val parroquias: LiveData<List<Parroquia>> get() = _parroquias

    private val _errores = MutableLiveData<Map<String, String>>()
    val errores: LiveData<Map<String, String>> = _errores
    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> = _salir

    fun onCreate(idPaciente: String?, isEditable: Boolean) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                if (isEditable) {
                    val catalogosJob = launch { cargarCatalogos() }
                    catalogosJob.join()
                }
                if (idPaciente != null && idPaciente.isNotEmpty() && idPaciente.isNotBlank()) {
                    val pacienteJob = launch { obtenerPaciente(idPaciente) }
                    pacienteJob.join()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun obtenerPaciente(idPaciente: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)

            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = "Error al buscar pacientes. No se ha seleccionado una institución."
                _isLoading.value = false
                _salir.value = true
                return@launch
            }

            val paciente = getPacienteById(idUsuarioInstitucion.value ?: 0, idPaciente)
            if (paciente == null) {
                _mensaje.postValue("No se encontró el paciente")
                _salir.postValue(true)
                return@launch
            }
            _paciente.value = paciente
            _etnia.value = getEtnia(paciente.etniaId)
            _nacionalidad.value = getNacionalidad(paciente.nacionalidadId)
            _parroquia.value = getParroquia(paciente.parroquiaId)
            _municipio.value = getMunicipio(parroquia.value?.municipioId!!)
            _estado.value = getEstado(municipio.value?.estadoId!!)

            _isLoading.postValue(false)
        }
    }

    private fun cargarCatalogos() {
        cargarEtnias()
        cargarNacionalidades()
        cargarEstados()
    }

    private fun cargarEtnias() {
        viewModelScope.launch {
            val lista = getEtnias()
            _etnias.value = lista
        }
    }

    private fun cargarNacionalidades() {
        viewModelScope.launch {
            val lista = getNacionalidades()
            _nacionalidades.value = lista
        }
    }

    private fun cargarEstados() {
        viewModelScope.launch {
            val lista = getEstados()
            _estados.value = lista
        }
    }

    fun cargarMunicipios(idEstado: Int) {
        viewModelScope.launch {
            val lista = getMunicipios(idEstado)
            _municipios.value = lista
        }
    }

    fun cargarParroquias(idMunicipio: Int) {
        viewModelScope.launch {
            val lista = getParroquias(idMunicipio)
            _parroquias.value = lista
        }
    }

    fun guardarPaciente(paciente: Paciente) {
        val erroresMap = validarDatosPaciente(paciente)
        if (erroresMap.isNotEmpty()) {
            _mensaje.value = "Corrige los campos en rojo."
            return
        }

        formatearDatosPaciente(paciente)

        viewModelScope.launch {
            _isLoading.value = true

            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                val id = institutionId
                paciente.usuarioInstitucionId = id
            } ?: run {
                _mensaje.postValue("Error al guardar el paciente. No se ha seleccionado una institución.")
                _salir.postValue(false)
                return@launch
            }

            val exitsCedula = getPacienteByCedula(paciente.usuarioInstitucionId, paciente.cedula)
            if (exitsCedula != null && exitsCedula.id != paciente.id) {
                _mensaje.postValue("Ya existe un paciente con la cédula ${paciente.cedula}.")
                _salir.postValue(false)
                return@launch
            }

            try {
                savePaciente(paciente)
                _mensaje.postValue("Paciente guardado correctamente.")
                _salir.postValue(true)

            } catch (e: DomainException) {
                _mensaje.postValue(e.message)
            } catch (e: Exception) {
                _mensaje.postValue("Ocurrió un error inesperado: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }

        }
    }

    private fun validarDatosPaciente(paciente: Paciente): Map<String, String> {
        val erroresActuales = _errores.value?.toMutableMap() ?: mutableMapOf()
        erroresActuales.clear()

        if (paciente.cedula.isBlank()) {
            erroresActuales["cedula"] = "La cédula es obligatoria."
        } else if (!esCedulaValida(paciente.cedula)) {
            erroresActuales["cedula"] = "La cédula no es válida."
        }

        if (paciente.nombres.isBlank()) {
            erroresActuales["nombres"] = "El nombre es obligatorio."
        }

        if (paciente.apellidos.isBlank()) {
            erroresActuales["apellidos"] = "El apellido es obligatorio."
        }

        if (paciente.fechaNacimiento.isAfter(LocalDate.now())) {
            erroresActuales["fechaNacimiento"] = "La fecha no puede ser futura."
        }

        if (paciente.genero.isBlank()) {
            erroresActuales["genero"] = "El género es obligatorio."
        }

        if (paciente.etniaId == 0) {
            erroresActuales["etnia"] = "La etnia es obligatoria."
        }

        if (paciente.nacionalidadId == 0) {
            erroresActuales["nacionalidad"] = "La nacionalidad es obligatoria."
        }

        if (paciente.parroquiaId == 0) {
            erroresActuales["parroquia"] = "La parroquia es obligatoria."
        }

        if (!paciente.telefono.isNullOrBlank() && !esNumeroTelefonoValido(paciente.telefono)) {
            erroresActuales["telefono"] = "El teléfono no es válido."
        }

        if (!paciente.correo.isNullOrBlank() && !esCorreoValido(paciente.correo)) {
            erroresActuales["correo"] = "El correo no es válido."
        }

        _errores.value = erroresActuales
        return erroresActuales
    }

    private fun formatearDatosPaciente(p: Paciente) {
        p.cedula = formatearCedula(p.cedula)
        p.nombres = p.nombres.uppercase().trim()
        p.apellidos = p.apellidos.uppercase().trim()
        p.telefono = formatearTelefono(p.telefono?.trim())
        p.correo = p.correo?.lowercase()?.trim()
    }
}
