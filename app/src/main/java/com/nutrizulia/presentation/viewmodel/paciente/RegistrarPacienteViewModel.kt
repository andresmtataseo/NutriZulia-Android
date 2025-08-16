package com.nutrizulia.presentation.viewmodel.paciente

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.exception.DomainException
import com.nutrizulia.domain.model.catalog.Estado
import com.nutrizulia.domain.model.catalog.Etnia
import com.nutrizulia.domain.model.catalog.Municipio
import com.nutrizulia.domain.model.catalog.Nacionalidad
import com.nutrizulia.domain.model.catalog.Parentesco
import com.nutrizulia.domain.model.catalog.Parroquia
import com.nutrizulia.domain.model.collection.Paciente
import com.nutrizulia.domain.model.collection.PacienteRepresentante
import com.nutrizulia.domain.model.collection.Representante
import com.nutrizulia.domain.usecase.catalog.GetEstadoById
import com.nutrizulia.domain.usecase.catalog.GetEstados
import com.nutrizulia.domain.usecase.catalog.GetEtniaById
import com.nutrizulia.domain.usecase.catalog.GetEtnias
import com.nutrizulia.domain.usecase.catalog.GetMunicipioById
import com.nutrizulia.domain.usecase.catalog.GetMunicipios
import com.nutrizulia.domain.usecase.catalog.GetNacionalidadById
import com.nutrizulia.domain.usecase.catalog.GetNacionalidades
import com.nutrizulia.domain.usecase.catalog.GetParentescoById
import com.nutrizulia.domain.usecase.catalog.GetParroquiaById
import com.nutrizulia.domain.usecase.catalog.GetParroquias
import com.nutrizulia.domain.usecase.collection.CountPacientesByRepresentante
import com.nutrizulia.domain.usecase.collection.GetPacienteByCedula
import com.nutrizulia.domain.usecase.collection.GetPacienteById
import com.nutrizulia.domain.usecase.collection.GetPacienteRepresentanteByPacienteId
import com.nutrizulia.domain.usecase.collection.GetRepresentanteById
import com.nutrizulia.domain.usecase.collection.SavePaciente
import com.nutrizulia.domain.usecase.collection.SavePacienteRepresentante
import com.nutrizulia.domain.usecase.user.GetCurrentInstitutionIdUseCase
import com.nutrizulia.util.CheckData
import com.nutrizulia.util.CheckData.esCedulaValida
import com.nutrizulia.util.CheckData.esCorreoValido
import com.nutrizulia.util.CheckData.esNumeroTelefonoValido
import com.nutrizulia.util.FormatData.formatearCedula
import com.nutrizulia.util.FormatData.formatearTelefono
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
class RegistrarPacienteViewModel @Inject constructor(
    private val savePaciente: SavePaciente,
    private val getPacienteByCedula: GetPacienteByCedula,
    private val getPacienteById: GetPacienteById,
    private val getPacienteRepresentanteByPacienteId: GetPacienteRepresentanteByPacienteId,
    private val getRepresentanteById: GetRepresentanteById,
    private val countPacientesByRepresentante: CountPacientesByRepresentante,
    private val savePacienteRepresentante: SavePacienteRepresentante,
    private val getParentescoById: GetParentescoById,
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

    private val _paciente = MutableLiveData<Paciente>()
    val paciente: LiveData<Paciente> = _paciente
    private val _esCedulado = MutableLiveData<Boolean>()
    val esCedulado: LiveData<Boolean> = _esCedulado
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

    //representante
    private val _representante = MutableLiveData<Representante?>()
    val representante: LiveData<Representante?> = _representante
    private val _cedulaTemporal = MutableLiveData<String?>()
    val cedulaTemporal: LiveData<String?> = _cedulaTemporal
    private val _selectedParentesco = MutableLiveData<Parentesco?>()
    val selectedParentesco: LiveData<Parentesco?> = _selectedParentesco
    private val _selectedEtniaR = MutableLiveData<Etnia?>()
    val selectedEtniaR: LiveData<Etnia?> = _selectedEtniaR
    private val _selectedNacionalidadR = MutableLiveData<Nacionalidad?>()
    val selectedNacionalidadR: LiveData<Nacionalidad?> = _selectedNacionalidadR
    private val _selectedEstadoR = MutableLiveData<Estado?>()
    val selectedEstadoR: LiveData<Estado?> = _selectedEstadoR
    private val _selectedMunicipioR = MutableLiveData<Municipio?>()
    val selectedMunicipioR: LiveData<Municipio?> = _selectedMunicipioR
    private val _selectedParroquiaR = MutableLiveData<Parroquia?>()
    val selectedParroquiaR: LiveData<Parroquia?> = _selectedParroquiaR

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

    fun onCreate(pacienteId: String?, isEditable: Boolean) {
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
                    catalogsJob?.await()
                    
                    // Ejecutar obtenerPaciente después de que _idUsuarioInstitucion esté asignado
                    if (!pacienteId.isNullOrBlank()) {
                        obtenerPaciente(pacienteId, institutionId)
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun obtenerPaciente(idPaciente: String, usuarioInstitucionId: Int) {
        val loadedPaciente = getPacienteById(usuarioInstitucionId, idPaciente)
        if (loadedPaciente == null) {
            _mensaje.postValue("No se encontró el paciente.")
            _salir.postValue(true)
            return
        }
        _paciente.postValue(loadedPaciente)

        // Verificar si el paciente tiene cédula temporal
        val tieneCedulaTemporal = esCedulaTemporal(loadedPaciente.cedula)
        _esCedulado.postValue(!tieneCedulaTemporal)
        
        if (tieneCedulaTemporal) {
            // Cargar información del representante sin generar nueva cédula temporal
            val pacienteRepresentante = getPacienteRepresentanteByPacienteId(idPaciente)
            if (pacienteRepresentante != null) {
                cargarRepresentanteExistente(pacienteRepresentante.representanteId, pacienteRepresentante.parentescoId, loadedPaciente.cedula)
            }
        }

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
    
    private fun esCedulaTemporal(cedula: String): Boolean {
        // Las cédulas temporales tienen el formato: cedulaRepresentante-XX (donde XX son 2 dígitos)
        return cedula.matches(Regex(".*-\\d{2}$"))
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

    fun validateCedulaRealTime(tipoCedula: String, cedula: String) {
        cedulaValidationJob?.cancel()
        
        if (cedula.isBlank()) {
            _cedulaValidationState.value = CedulaValidationState.IDLE
            return
        }
        
        cedulaValidationJob = viewModelScope.launch {
            try {
                _cedulaValidationState.value = CedulaValidationState.VALIDATING
                delay(500) // Debounce de 500ms
                
                // Rellenar con ceros a la izquierda si tiene menos de 8 dígitos
                val cedulaFormateada = if (cedula.length < 8 && cedula.all { it.isDigit() }) {
                    cedula.padStart(8, '0')
                } else {
                    cedula
                }
                
                val cedulaCompleta = "$tipoCedula-$cedulaFormateada"
                
                // Validar formato de cédula
                if (!esCedulaValida(cedulaCompleta)) {
                    _cedulaValidationState.value = CedulaValidationState.INVALID
                    return@launch
                }
                
                // Verificar unicidad
                val institutionId = getCurrentInstitutionId()
                if (institutionId != null) {
                    val pacienteExistente = getPacienteByCedula(institutionId, cedulaCompleta)
                    if (pacienteExistente != null && pacienteExistente.id != _paciente.value?.id) {
                        _cedulaValidationState.value = CedulaValidationState.DUPLICATE
                    } else {
                        _cedulaValidationState.value = CedulaValidationState.VALID
                    }
                } else {
                    _cedulaValidationState.value = CedulaValidationState.INVALID
                }
            } catch (e: Exception) {
                _cedulaValidationState.value = CedulaValidationState.INVALID
            }
        }
    }

    fun onSavePatientClicked(id: String?, esCedulado: Boolean?, tipoCedula: String, cedula: String, nombres: String, apellidos: String, fechaNacimientoStr: String, genero: String, domicilio: String, prefijo: String, telefono: String, correo: String) {
        _errores.value = emptyMap()
        val fechaNacimiento: LocalDate? = try { LocalDate.parse(fechaNacimientoStr) } catch (e: DateTimeParseException) { null }
        
        // Determinar la cédula a usar
        val cedulaFinal = if (esCedulado == true) {
            if (tipoCedula.isBlank() || cedula.isBlank()) {
                ""
            } else {
                // Rellenar con ceros a la izquierda si tiene menos de 8 dígitos
                val cedulaFormateada = if (cedula.length < 8 && cedula.all { it.isDigit() }) {
                    cedula.padStart(8, '0')
                } else {
                    cedula
                }
                "$tipoCedula-$cedulaFormateada"
            }
        } else {
            // Para pacientes no cedulados, usar la cédula temporal del representante
            _cedulaTemporal.value ?: ""
        }
        
        val pacienteToSave = Paciente(
            id = id ?: Utils.generarUUID(),
            usuarioInstitucionId = _idUsuarioInstitucion.value ?: 0,
            cedula = cedulaFinal,
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
        val erroresMap = validarDatosPaciente(pacienteToSave, fechaNacimiento == null, esCedulado, tipoCedula, cedula)
        if (erroresMap.isNotEmpty()) {
            _errores.value = erroresMap
            val cantidadErrores = erroresMap.size
            val mensaje = if (cantidadErrores == 1) {
                "Hay 1 campo que requiere atención. Revisa el campo marcado en rojo."
            } else {
                "Hay $cantidadErrores campos que requieren atención. Revisa los campos marcados en rojo."
            }
            _mensaje.value = mensaje
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
                
                // Si el paciente no está cedulado, guardar la relación con el representante
                if (esCedulado == false && _representante.value != null && _selectedParentesco.value != null) {
                    val pacienteRepresentante = PacienteRepresentante(
                        id = Utils.generarUUID(),
                        usuarioInstitucionId = institutionId,
                        pacienteId = pacienteToSave.id,
                        representanteId = _representante.value!!.id,
                        parentescoId = _selectedParentesco.value!!.id,
                        updatedAt = LocalDateTime.now(),
                        isDeleted = false,
                        isSynced = false
                    )
                    savePacienteRepresentante(pacienteRepresentante)
                }
                
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

    private fun validarDatosPaciente(paciente: Paciente, fechaInvalida: Boolean, esCedulado: Boolean?, tipoCedula: String, cedula: String): Map<String, String> {
        val errores = mutableMapOf<String, String>()
        
        // Validar que se haya seleccionado si es cedulado o no
        if (esCedulado == null) {
            errores["esCedulado"] = "Debe indicar si el paciente está cedulado"
            return errores // Retornar inmediatamente si no se ha seleccionado
        }
        
        // Validar campos básicos
        if (paciente.nombres.isBlank()) errores["nombres"] = "El nombre es obligatorio"
        if (paciente.apellidos.isBlank()) errores["apellidos"] = "El apellido es obligatorio"
        if (fechaInvalida) errores["fechaNacimiento"] = "La fecha de nacimiento es obligatoria"
        if (paciente.genero.isBlank()) errores["genero"] = "El género es obligatorio"
        
        // Validar campos específicos para pacientes cedulados
        if (esCedulado) {
            if (tipoCedula.isBlank()) errores["tipoCedula"] = "El tipo de cédula es obligatorio"
            if (cedula.isBlank()) errores["cedula"] = "La cédula es obligatoria"
            else {
                // Formar la cédula completa para validar
                val cedulaCompleta = "$tipoCedula-$cedula"
                if (!esCedulaValida(cedulaCompleta)) errores["cedula"] = "La cédula no es válida"
            }
        } else {
            // Para pacientes no cedulados, validar que tengan representante
            if (_representante.value == null) {
                errores["representante"] = "Debe seleccionar un representante para pacientes no cedulados"
            }
            if (_selectedParentesco.value == null) {
                errores["parentesco"] = "Debe seleccionar el parentesco con el representante"
            }
        }
        
        // Validar campos opcionales si están llenos
        if (!paciente.telefono.isNullOrBlank() && !esNumeroTelefonoValido(paciente.telefono)) {
            errores["telefono"] = "El número de teléfono no es válido"
        }
        if (!paciente.correo.isNullOrBlank() && !esCorreoValido(paciente.correo)) {
            errores["correo"] = "El correo electrónico no es válido"
        }
        
        // Validar selecciones de catálogos
        if (_selectedEtnia.value == null) errores["etnia"] = "La etnia es obligatoria"
        if (_selectedNacionalidad.value == null) errores["nacionalidad"] = "La nacionalidad es obligatoria"
        if (_selectedEstado.value == null) errores["estado"] = "El estado es obligatorio"
        if (_selectedMunicipio.value == null) errores["municipio"] = "El municipio es obligatorio"
        if (_selectedParroquia.value == null) errores["parroquia"] = "La parroquia es obligatoria"
        
        return errores
    }

    private fun formatearDatosPaciente(p: Paciente) {
        p.cedula = formatearCedula(p.cedula)
        p.nombres = p.nombres.uppercase().trim()
        p.apellidos = p.apellidos.uppercase().trim()
        p.telefono = formatearTelefono(p.telefono?.trim())
        p.correo = p.correo?.lowercase()?.trim()
    }

    // Representante

    fun onRepresentanteSelected(representanteId: String, parentescoId: Int) {
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
                    val representanteJob = if (representanteId.isNotBlank()) async { obtenerRepresentante(representanteId, parentescoId) } else null
                    representanteJob?.await()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun obtenerRepresentante(representanteId: String, parentescoId: Int) {
        val usuarioInstitucionId = _idUsuarioInstitucion.value
        if (usuarioInstitucionId == null) {
            _mensaje.postValue("Error: No se ha inicializado la institución.")
            _salir.postValue(true)
            return
        }
        
        val loadedRepresentante = getRepresentanteById(usuarioInstitucionId, representanteId)
        if (loadedRepresentante == null) {
            _mensaje.postValue("No se encontró el representante.")
            _salir.postValue(true)
            return
        }
        _representante.postValue(loadedRepresentante)
        val cedulaTemporal = generarCedulaTemporal(loadedRepresentante.id, loadedRepresentante.cedula)
        val parroquia = getParroquiaById(loadedRepresentante.parroquiaId)
        val municipio = parroquia?.let { getMunicipioById(it.municipioId) }
        val estado = municipio?.let { getEstadoById(it.estadoId) }
        _cedulaTemporal.postValue(cedulaTemporal)
        _selectedParentesco.postValue(getParentescoById(parentescoId))
        _selectedEtniaR.postValue(getEtniaById(loadedRepresentante.etniaId))
        _selectedNacionalidadR.postValue(getNacionalidadById(loadedRepresentante.nacionalidadId))
        _selectedEstadoR.postValue(estado)
        _selectedMunicipioR.postValue(municipio)
        _selectedParroquiaR.postValue(parroquia)
        if (estado != null) _municipios.postValue(getMunicipios(estado.id))
        if (municipio != null) _parroquias.postValue(getParroquias(municipio.id))
    }

    private suspend fun cargarRepresentanteExistente(representanteId: String, parentescoId: Int, cedulaExistente: String) {
        val usuarioInstitucionId = _idUsuarioInstitucion.value
        if (usuarioInstitucionId == null) {
            _mensaje.postValue("Error: No se ha inicializado la institución.")
            _salir.postValue(true)
            return
        }
        
        val loadedRepresentante = getRepresentanteById(usuarioInstitucionId, representanteId)
        if (loadedRepresentante == null) {
            _mensaje.postValue("No se encontró el representante.")
            _salir.postValue(true)
            return
        }
        _representante.postValue(loadedRepresentante)
        val parroquia = getParroquiaById(loadedRepresentante.parroquiaId)
        val municipio = parroquia?.let { getMunicipioById(it.municipioId) }
        val estado = municipio?.let { getEstadoById(it.estadoId) }
        _cedulaTemporal.postValue(cedulaExistente)
        _selectedParentesco.postValue(getParentescoById(parentescoId))
        _selectedEtniaR.postValue(getEtniaById(loadedRepresentante.etniaId))
        _selectedNacionalidadR.postValue(getNacionalidadById(loadedRepresentante.nacionalidadId))
        _selectedEstadoR.postValue(estado)
        _selectedMunicipioR.postValue(municipio)
        _selectedParroquiaR.postValue(parroquia)
        if (estado != null) _municipios.postValue(getMunicipios(estado.id))
        if (municipio != null) _parroquias.postValue(getParroquias(municipio.id))
    }

    private suspend fun generarCedulaTemporal(representanteId: String, cedulaRepresentante: String): String {
        val usuarioInstitucionId = _idUsuarioInstitucion.value ?: return ""
        val count = countPacientesByRepresentante(usuarioInstitucionId, representanteId)
        val nextNumber = (count + 1).toString().padStart(2, '0')
        return "$cedulaRepresentante-$nextNumber"
    }

    fun limpiarRepresentante() {
        _representante.value = null
        _cedulaTemporal.value = null
        _selectedParentesco.value = null
        _selectedEtniaR.value = null
        _selectedNacionalidadR.value = null
        _selectedEstadoR.value = null
        _selectedMunicipioR.value = null
        _selectedParroquiaR.value = null
    }
    
    fun permitirCambioRepresentante(): Boolean {
        // Permitir cambio de representante si el paciente no es cedulado
        return _esCedulado.value == false
    }

}