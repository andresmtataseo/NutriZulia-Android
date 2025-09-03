package com.nutrizulia.presentation.viewmodel.representante

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
import com.nutrizulia.domain.model.collection.Representante
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
import com.nutrizulia.domain.usecase.collection.GetRepresentanteByCedula
import com.nutrizulia.domain.usecase.collection.GetRepresentanteById
import com.nutrizulia.domain.usecase.collection.SaveRepresentante
import com.nutrizulia.domain.usecase.user.GetCurrentInstitutionIdUseCase
import com.nutrizulia.presentation.viewmodel.paciente.RegistrarPacienteViewModel
import com.nutrizulia.util.CheckData
import com.nutrizulia.util.CheckData.esCedulaValida
import com.nutrizulia.util.FormatData
import com.nutrizulia.util.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class RegistrarRepresentanteViewModel @Inject constructor(
    private val saveRepresentante: SaveRepresentante,
    private val getRepresentanteByCedula: GetRepresentanteByCedula,
    private val getRepresentanteById: GetRepresentanteById,
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

    // Necesarios
    private val _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion
    private val _representante = MutableLiveData<Representante>()
    val representante: LiveData<Representante> = _representante
    // catalogo
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
    // seleccion
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
    // Ui states
    private val _filtro = MutableLiveData<String>()
    val filtro: LiveData<String> get() = _filtro
    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje
    private val _errores = MutableLiveData<Map<String, String>>()
    val errores: LiveData<Map<String, String>> = _errores
    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> = _salir
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    // Validación en tiempo real de cédula
    private val _cedulaValidationState = MutableLiveData<CedulaValidationState>()
    val cedulaValidationState: LiveData<CedulaValidationState> = _cedulaValidationState
    private var cedulaValidationJob: Job? = null
    enum class CedulaValidationState {
        IDLE,
        VALIDATING,
        VALID,
        DUPLICATE,
        INVALID
    }

    fun onCreate(representanteId: String?, isEditable: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val institutionId: Int? = getCurrentInstitutionId()
                if (institutionId == null) {
                    _mensaje.postValue("Error: No se ha seleccionado una institución.")
                    _salir.postValue(true)
                    return@launch
                }
                _idUsuarioInstitucion.postValue(institutionId)
                coroutineScope {
                    val catalogsJob = if (isEditable) async { cargarCatalogos() } else null
                    val representanteJob = if (!representanteId.isNullOrBlank()) async { obtenerRepresentante(representanteId, institutionId) } else null
                    catalogsJob?.await()
                    representanteJob?.await()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun obtenerRepresentante(representanteId: String, institutionId: Int) {
        val loadedRepresentante = getRepresentanteById(institutionId, representanteId)
        if (loadedRepresentante == null) {
            _mensaje.postValue("No se encontró el paciente.")
            _salir.postValue(true)
            return
        }
        _representante.postValue(loadedRepresentante)

        val parroquia = getParroquiaById(loadedRepresentante.parroquiaId)
        val municipio = parroquia?.let { getMunicipioById(it.municipioId) }
        val estado = municipio?.let { getEstadoById(it.estadoId) }

        _selectedEtnia.postValue(getEtniaById(loadedRepresentante.etniaId))
        _selectedNacionalidad.postValue(getNacionalidadById(loadedRepresentante.nacionalidadId))
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
        viewModelScope.launch {
            val institutionId = getCurrentInstitutionId()
            if (institutionId == null) {
                _mensaje.value = "No se ha seleccionado una institución."
                return@launch
            }
            
            _errores.value = emptyMap()
            val fechaNacimiento: LocalDate? = try { LocalDate.parse(fechaNacimientoStr) } catch (e: DateTimeParseException) { null }
            val representanteToSave = Representante(
                id = id ?: Utils.generarUUID(),
                usuarioInstitucionId = institutionId,
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
                updatedAt = LocalDateTime.now(),
                isDeleted = false,
                isSynced = false
            )
            val erroresMap = validarDatosRepresentante(representanteToSave, fechaNacimiento == null)
            if (erroresMap.isNotEmpty()) {
                _errores.value = erroresMap
                _mensaje.value = "Corrige los campos en rojo."
                return@launch
            }
            formatearDatosRepresentante(representanteToSave)

            _isLoading.value = true
            val cedulaExistente =
                getRepresentanteByCedula(institutionId, representanteToSave.cedula)
            if (cedulaExistente != null && cedulaExistente.id != representanteToSave.id) {
                _mensaje.value = "Ya existe un paciente con la cédula ${representanteToSave.cedula}."
                _isLoading.value = false
                return@launch
            }
            try {
                saveRepresentante(representanteToSave)
                _mensaje.value = "Representante guardado correctamente."
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

    private fun validarDatosRepresentante(representante: Representante, isFechaInvalida: Boolean): Map<String, String> {
        val erroresActuales = mutableMapOf<String, String>()
        if (representante.cedula.isBlank() || representante.cedula == "-") {
            erroresActuales["cedula"] = "La cédula es obligatoria."
        } else if (!CheckData.esCedulaValida(representante.cedula)) {
            erroresActuales["cedula"] = "La cédula no es válida."
        }
        if (representante.nombres.isBlank()) erroresActuales["nombres"] = "El nombre es obligatorio."
        if (representante.apellidos.isBlank()) erroresActuales["apellidos"] = "El apellido es obligatorio."
        if (isFechaInvalida) erroresActuales["fechaNacimiento"] = "La fecha no es válida o está vacía."
        if (representante.genero.isBlank()) erroresActuales["genero"] = "El género es obligatorio."
        if (representante.etniaId == 0) erroresActuales["etnia"] = "La etnia es obligatoria."
        if (representante.nacionalidadId == 0) erroresActuales["nacionalidad"] = "La nacionalidad es obligatoria."
        if (representante.parroquiaId == 0) erroresActuales["parroquia"] = "La parroquia es obligatoria."
        if (!representante.telefono.isNullOrBlank() && !CheckData.esNumeroTelefonoValido(
                representante.telefono
            )
        ) erroresActuales["telefono"] = "El teléfono no es válido."
        if (!representante.correo.isNullOrBlank() && !CheckData.esCorreoValido(representante.correo)) erroresActuales["correo"] = "El correo no es válido."
        return erroresActuales
    }

    private fun formatearDatosRepresentante(representante: Representante) {
        representante.cedula = FormatData.formatearCedula(representante.cedula)
        representante.nombres = representante.nombres.uppercase().trim()
        representante.apellidos = representante.apellidos.uppercase().trim()
        representante.telefono = FormatData.formatearTelefono(representante.telefono?.trim())
        representante.correo = representante.correo?.lowercase()?.trim()
    }

    fun validateCedulaRealTime(tipoCedula: String, cedula: String) {
        cedulaValidationJob?.cancel()

        if (cedula.isBlank()) {
            _cedulaValidationState.value = RegistrarRepresentanteViewModel.CedulaValidationState.IDLE
            return
        }

        cedulaValidationJob = viewModelScope.launch {
            try {
                _cedulaValidationState.value = RegistrarRepresentanteViewModel.CedulaValidationState.VALIDATING
                delay(500)

                val cedulaFormateada = if (cedula.length < 8 && cedula.all { it.isDigit() }) {
                    cedula.padStart(8, '0')
                } else {
                    cedula
                }

                val cedulaCompleta = "$tipoCedula-$cedulaFormateada"

                // Validar formato de cédula
                if (!esCedulaValida(cedulaCompleta)) {
                    _cedulaValidationState.value = RegistrarRepresentanteViewModel.CedulaValidationState.INVALID
                    return@launch
                }

                // Verificar unicidad
                val institutionId = getCurrentInstitutionId()
                if (institutionId != null) {
                    val representanteExistente = getRepresentanteByCedula(institutionId, cedulaCompleta)
                    if (representanteExistente != null && representanteExistente.id != _representante.value?.id) {
                        _cedulaValidationState.value = RegistrarRepresentanteViewModel.CedulaValidationState.DUPLICATE
                    } else {
                        _cedulaValidationState.value = RegistrarRepresentanteViewModel.CedulaValidationState.VALID
                    }
                } else {
                    _cedulaValidationState.value = RegistrarRepresentanteViewModel.CedulaValidationState.INVALID
                }
            } catch (e: Exception) {
                _cedulaValidationState.value = RegistrarRepresentanteViewModel.CedulaValidationState.INVALID
            }
        }
    }

}