package com.nutrizulia.presentation.viewmodel

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
import com.nutrizulia.domain.usecase.user.GetCurrentInstitutionIdUseCase
import com.nutrizulia.util.CheckData.esCedulaValida
import com.nutrizulia.util.CheckData.esCorreoValido
import com.nutrizulia.util.CheckData.esNumeroTelefonoValido
import com.nutrizulia.util.FormatData.formatearCedula
import com.nutrizulia.util.FormatData.formatearTelefono
import com.nutrizulia.util.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class RegistrarPacienteViewModel @Inject constructor(
    private val savePaciente: SavePaciente,
    private val getPacienteByCedula: GetPacienteByCedula,
    private val getPacienteById: GetPacienteById,
    private val getEtniaById: GetEtniaById,
    private val getNacionalidadById: GetNacionalidadById,
    private val getEstadoById: GetEstadoById,
    private val getMunicipioById: GetMunicipioById,
    private val getParroquiaById: GetParroquiaById,
    private val getEtnias: GetEtnias,
    private val getNacionalidades: GetNacionalidades,
    private val getEstados: GetEstados,
    private val getMunicipios: GetMunicipios,
    private val getParroquias: GetParroquias,
    private val getCurrentInstitutionId: GetCurrentInstitutionIdUseCase
) : ViewModel() {

    // --- LiveData sin cambios ---
    private val _paciente = MutableLiveData<Paciente>()
    val paciente: LiveData<Paciente> = _paciente
    private val _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion
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
    private val _selectedEtnia = MutableLiveData<Etnia?>()
    val selectedEtnia: LiveData<Etnia?> = _selectedEtnia
    private val _selectedNacionalidad = MutableLiveData<Nacionalidad?>()
    val selectedNacionalidad: LiveData<Nacionalidad?> = _selectedNacionalidad
    private val _selectedEstado = MutableLiveData<Estado?>()
    val selectedEstado: LiveData<Estado?> = _selectedEstado
    private val _selectedMunicipio = MutableLiveData<Municipio?>()
    val selectedMunicipio: LiveData<Municipio?> = _selectedMunicipio
    private val _selectedParroquia = MutableLiveData<Parroquia?>()
    val selectedParroquia: LiveData<Parroquia?> = _selectedParroquia

    fun onCreate(pacienteId: String?, isEditable: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                coroutineScope {
                    val catalogsJob = if (isEditable) async { cargarCatalogos() } else null
                    val patientJob = if (!pacienteId.isNullOrBlank()) async { obtenerPaciente(pacienteId) } else null
                    catalogsJob?.await()
                    patientJob?.await()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun obtenerPaciente(idPaciente: String) {
        val institutionId = getCurrentInstitutionId()
        if (institutionId == null) {
            _mensaje.postValue("Error: No se ha seleccionado una institución.")
            _salir.postValue(true)
            return
        }
        _idUsuarioInstitucion.postValue(institutionId)

        val loadedPaciente = getPacienteById(institutionId, idPaciente)
        if (loadedPaciente == null) {
            _mensaje.postValue("No se encontró el paciente.")
            _salir.postValue(true)
            return
        }
        _paciente.postValue(loadedPaciente)

        val parroquia = getParroquiaById(loadedPaciente.parroquiaId)
        val municipio = parroquia?.let { getMunicipioById(it.municipioId) }
        val estado = municipio?.let { getEstadoById(it.estadoId) }

        _selectedEtnia.postValue(getEtniaById(loadedPaciente.etniaId))
        _selectedNacionalidad.postValue(getNacionalidadById(loadedPaciente.nacionalidadId))
        _selectedEstado.postValue(estado)
        _selectedMunicipio.postValue(municipio)
        _selectedParroquia.postValue(parroquia)

        if (estado != null) _municipios.postValue(getMunicipios(estado.id))
        if (municipio != null) _parroquias.postValue(getParroquias(municipio.id))
    }

    private suspend fun cargarCatalogos() {
        coroutineScope {
            val etniasJob = async { getEtnias() }
            val nacionalidadesJob = async { getNacionalidades() }
            val estadosJob = async { getEstados() }

            _etnias.postValue(etniasJob.await())
            _nacionalidades.postValue(nacionalidadesJob.await())
            _estados.postValue(estadosJob.await())
        }
    }

    fun onEtniaSelected(etnia: Etnia) { _selectedEtnia.value = etnia }
    fun onNacionalidadSelected(nacionalidad: Nacionalidad) { _selectedNacionalidad.value = nacionalidad }
    fun onParroquiaSelected(parroquia: Parroquia) { _selectedParroquia.value = parroquia }

    fun onTipoCedulaSelected(tipoCedula: String) {
        if (tipoCedula == "V") {
            val venezuela = _nacionalidades.value?.find { it.id == 239 }
            _selectedNacionalidad.value = venezuela
        }
    }

    fun onEstadoSelected(estado: Estado, isInitialLoad: Boolean = false) {
        _selectedEstado.value = estado
        if (!isInitialLoad) {
            _selectedMunicipio.value = null
            _selectedParroquia.value = null
        }
        viewModelScope.launch { _municipios.value = getMunicipios(estado.id) }
    }

    fun onMunicipioSelected(municipio: Municipio, isInitialLoad: Boolean = false) {
        _selectedMunicipio.value = municipio
        if (!isInitialLoad) {
            _selectedParroquia.value = null
        }
        viewModelScope.launch { _parroquias.value = getParroquias(municipio.id) }
    }

    fun onSavePatientClicked(id: String?, tipoCedula: String, cedula: String, nombres: String, apellidos: String, fechaNacimientoStr: String, genero: String, domicilio: String, prefijo: String, telefono: String, correo: String) {
        _errores.value = emptyMap()
        val fechaNacimiento: LocalDate? = try { LocalDate.parse(fechaNacimientoStr) } catch (e: DateTimeParseException) { null }
        val pacienteToSave = Paciente(
            id = id ?: Utils.generarUUID(),
            usuarioInstitucionId = 0,
            cedula = "$tipoCedula-$cedula",
            nombres = nombres,
            apellidos = apellidos,
            fechaNacimiento = fechaNacimiento ?: LocalDate.MIN,
            genero = genero,
            etniaId = _selectedEtnia.value?.id ?: 0,
            nacionalidadId = _selectedNacionalidad.value?.id ?: 0,
            parroquiaId = _selectedParroquia.value?.id ?: 0,
            domicilio = domicilio,
            telefono = if (telefono.isNotBlank()) "$prefijo$telefono" else "",
            correo = correo,
            updatedAt = LocalDateTime.now()
        )
        val erroresMap = validarDatosPaciente(pacienteToSave, fechaNacimiento == null)
        if (erroresMap.isNotEmpty()) {
            _errores.value = erroresMap
            _mensaje.value = "Corrige los campos en rojo."
            return
        }
        formatearDatosPaciente(pacienteToSave)

        viewModelScope.launch {
            _isLoading.value = true
            val institutionId = getCurrentInstitutionId() ?: run {
                _mensaje.value = "Error al guardar: No se ha seleccionado una institución."
                _isLoading.value = false
                return@launch
            }
            pacienteToSave.usuarioInstitucionId = institutionId

            val cedulaExistente = getPacienteByCedula(institutionId, pacienteToSave.cedula)
            if (cedulaExistente != null && cedulaExistente.id != pacienteToSave.id) {
                _mensaje.value = "Ya existe un paciente con la cédula ${pacienteToSave.cedula}."
                _isLoading.value = false
                return@launch
            }
            try {
                savePaciente(pacienteToSave)
                _mensaje.value = "Paciente guardado correctamente."
                _salir.value = true
            } catch (e: DomainException) {
                _mensaje.value = e.message
            } catch (e: Exception) {
                _mensaje.value = "Ocurrió un error inesperado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun validarDatosPaciente(paciente: Paciente, isFechaInvalida: Boolean): Map<String, String> {
        val erroresActuales = mutableMapOf<String, String>()
        if (paciente.cedula.isBlank() || paciente.cedula == "-") {
            erroresActuales["cedula"] = "La cédula es obligatoria."
        } else if (!esCedulaValida(paciente.cedula)) {
            erroresActuales["cedula"] = "La cédula no es válida."
        }
        if (paciente.nombres.isBlank()) erroresActuales["nombres"] = "El nombre es obligatorio."
        if (paciente.apellidos.isBlank()) erroresActuales["apellidos"] = "El apellido es obligatorio."
        if (isFechaInvalida) erroresActuales["fechaNacimiento"] = "La fecha no es válida o está vacía."
        if (paciente.genero.isBlank()) erroresActuales["genero"] = "El género es obligatorio."
        if (paciente.etniaId == 0) erroresActuales["etnia"] = "La etnia es obligatoria."
        if (paciente.nacionalidadId == 0) erroresActuales["nacionalidad"] = "La nacionalidad es obligatoria."
        if (paciente.parroquiaId == 0) erroresActuales["parroquia"] = "La parroquia es obligatoria."
        if (!paciente.telefono.isNullOrBlank() && !esNumeroTelefonoValido(paciente.telefono)) erroresActuales["telefono"] = "El teléfono no es válido."
        if (!paciente.correo.isNullOrBlank() && !esCorreoValido(paciente.correo)) erroresActuales["correo"] = "El correo no es válido."
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