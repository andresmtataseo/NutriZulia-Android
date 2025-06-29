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
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.usecase.catalog.GetEstados
import com.nutrizulia.domain.usecase.catalog.GetEtnias
import com.nutrizulia.domain.usecase.catalog.GetMunicipios
import com.nutrizulia.domain.usecase.catalog.GetNacionalidades
import com.nutrizulia.domain.usecase.catalog.GetParroquias
import com.nutrizulia.domain.usecase.collection.GetPacienteByCedula
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
    private val insertPacienteUseCase: SavePaciente,
    private val getPacienteByCedula: GetPacienteByCedula,
    private val getEtnias: GetEtnias,
    private val getNacionalidades: GetNacionalidades,
    private val getEstados: GetEstados,
    private val getMunicipios: GetMunicipios,
    private val getParroquias: GetParroquias,
    private val sessionManager: SessionManager
) : ViewModel() {

    val mensaje = MutableLiveData<String>()
    val errores = MutableLiveData<Map<String, String>>()
    val salir = MutableLiveData<Boolean>()

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

    fun cargarEtnias() {
        viewModelScope.launch {
            val lista = getEtnias()
            _etnias.value = lista
        }
    }

    fun cargarNacionalidades() {
        viewModelScope.launch {
            val lista = getNacionalidades()
            _nacionalidades.value = lista
        }
    }

    fun cargarEstados() {
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

    fun registrarPaciente(paciente: Paciente) {
        val erroresMap = validarDatosPaciente(paciente)
        if (erroresMap.isNotEmpty()) {
            errores.value = erroresMap
            mensaje.value = "Corrige los campos en rojo."
            return
        }

        formatearDatosPaciente(paciente)

        viewModelScope.launch {

            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                val id = institutionId
                paciente.usuarioInstitucionId = id
            } ?: run {
                mensaje.postValue("Error al actualizar el paciente. No se ha seleccionado una institución.")
                salir.postValue(false)
                return@launch
            }

            val exitsCedula = getPacienteByCedula(paciente.usuarioInstitucionId, paciente.cedula)
            if (exitsCedula != null && exitsCedula.id != paciente.id) {
                mensaje.postValue("Ya existe un paciente con la cédula ${paciente.cedula}.")
                salir.postValue(false)
                return@launch
            }

            try {
                val result = insertPacienteUseCase(paciente)
                if (result > 0) {
                    mensaje.postValue("Paciente registrado correctamente.")
                    salir.postValue(true)
                } else {
                    mensaje.postValue("Error desconocido al registrar paciente (código: $result).")
                    salir.postValue(false)
                }
            } catch (e: DomainException) {
                mensaje.postValue(e.message)
                salir.postValue(false)
            } catch (e: Exception) {
                mensaje.postValue("Ocurrió un error inesperado al registrar paciente: ${e.message}")
                salir.postValue(false)
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

        if (p.nombres.isBlank()) {
            errores["nombres"] = "El nombre es obligatorio."
        }

        if (p.apellidos.isBlank()) {
            errores["apellidos"] = "El apellido es obligatorio."
        }

        if (p.fechaNacimiento.isAfter(LocalDate.now())) {
            errores["fechaNacimiento"] = "La fecha no puede ser futura."
        }

        if (p.genero.isBlank()) {
            errores["genero"] = "El género es obligatorio."
        }

        if (p.etniaId == 0) {
            errores["etnia"] = "La etnia es obligatoria."
        }

        if (p.nacionalidadId == 0) {
            errores["nacionalidad"] = "La nacionalidad es obligatoria."
        }

        if (p.parroquiaId == 0) {
            errores["parroquia"] = "La parroquia es obligatoria."
        }

        if (!p.telefono.isNullOrBlank() && !esNumeroTelefonoValido(p.telefono)) {
            errores["telefono"] = "El teléfono no es válido."
        }

        if (!p.correo.isNullOrBlank() && !esCorreoValido(p.correo)) {
            errores["correo"] = "El correo no es válido."
        }

        return errores
    }

    private fun formatearDatosPaciente(p: Paciente) {
        p.cedula = formatearCedula(p.cedula)
        p.nombres = p.nombres.uppercase().trim()
        p.apellidos = p.apellidos.uppercase().trim()
        p.telefono = formatearTelefono(p.telefono?.trim())
        p.correo = p.correo?.lowercase()?.trim()
    }
}
