package com.nutrizulia.presentation.viewmodel.actividad

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.exception.DomainException
import com.nutrizulia.domain.model.catalog.TipoActividad
import com.nutrizulia.domain.model.collection.Actividad
import com.nutrizulia.domain.usecase.catalog.GetTipoActividadById
import com.nutrizulia.domain.usecase.collection.GetActividadById
import com.nutrizulia.domain.usecase.collection.GetTipoActividades
import com.nutrizulia.domain.usecase.collection.SaveActividad
import com.nutrizulia.domain.usecase.user.GetCurrentInstitutionIdUseCase
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
class RegistrarActividadViewModel @Inject constructor(
    private val saveActividad: SaveActividad,
    private val getActividadById: GetActividadById,
    private val getTipoActividadById: GetTipoActividadById,
    private val getTipoActividades: GetTipoActividades,
    private val getCurrentInstitutionId: GetCurrentInstitutionIdUseCase
) : ViewModel() {

    // Necesarios
    private val _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion
    private val _actividad = MutableLiveData<Actividad>()
    val actividad: LiveData<Actividad> = _actividad
    // catalogo
    private val _tipoActividades = MutableLiveData<List<TipoActividad>>()
    val tipoActividades: LiveData<List<TipoActividad>> get() = _tipoActividades
    // seleccion
    private val _selectedTipoActividad = MutableLiveData<TipoActividad?>()
    val selectTipoActividad: LiveData<TipoActividad?> = _selectedTipoActividad
    // visibilidad de campos
    private val _camposVisibles = MutableLiveData<Set<String>>()
    val camposVisibles: LiveData<Set<String>> = _camposVisibles
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

    fun onCreate(actividadId: String?, isEditable: Boolean) {
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
                    val actividadJob = if (!actividadId.isNullOrBlank()) async { obtenerActividad(actividadId, institutionId) } else null
                    catalogsJob?.await()
                    actividadJob?.await()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun obtenerActividad(actividadId: String, institutionId: Int) {
        val loadedActividad = getActividadById(actividadId, institutionId)
        if (loadedActividad == null) {
            _mensaje.postValue("No se encontró la actividad.")
            _salir.postValue(true)
            return
        }
        _actividad.postValue(loadedActividad)

        val tipoActividad = getTipoActividadById(loadedActividad.tipoActividadId)
        _selectedTipoActividad.postValue(tipoActividad)
        
        // Establecer los campos visibles basados en el tipo de actividad cargada
        tipoActividad?.let {
            _camposVisibles.postValue(obtenerCamposVisibles(it.nombre))
        }
    }

    private suspend fun cargarCatalogos() {
        coroutineScope {
            val getTipoActividadesJob = async { getTipoActividades() }
            val tiposActividad = getTipoActividadesJob.await()
            // Filtrar para excluir el tipo de actividad 'REGULAR'
            val tiposFiltrados = tiposActividad.filter { it.nombre != "REGULAR" }
            _tipoActividades.postValue(tiposFiltrados)
        }
    }

    fun onActividadSelected(tipoActividad: TipoActividad) { 
        _selectedTipoActividad.value = tipoActividad
        _camposVisibles.value = obtenerCamposVisibles(tipoActividad.nombre)
    }

    fun onSaveActividadClicked(
        id: String?,
        fechaStr: String,
        direccion: String?,
        descripcionGeneral: String?,
        cantidadParticipantes: Int?,
        cantidadSesiones: Int?,
        duracionMinutos: Int?,
        temaPrincipal: String?,
        programasImplementados: String?,
        urlEvidencia: String?
    ) {
        _errores.value = emptyMap()
        
        // Validar que la fecha no esté vacía antes de intentar parsearla
        if (fechaStr.isBlank()) {
            _errores.value = mapOf("fecha" to "La fecha es obligatoria.")
            _mensaje.value = "Corrige los campos en rojo."
            return
        }
        
        val fecha: LocalDate? = try { LocalDate.parse(fechaStr) } catch (e: DateTimeParseException) { null }

        val actividadToSave = Actividad(
            id = id ?: Utils.generarUUID(),
            usuarioInstitucionId = _idUsuarioInstitucion.value ?: 0,
            tipoActividadId = _selectedTipoActividad.value?.id ?: 0,
            fecha = fecha ?: LocalDate.now(),
            direccion = direccion,
            descripcionGeneral = descripcionGeneral,
            cantidadParticipantes = cantidadParticipantes,
            cantidadSesiones = cantidadSesiones,
            duracionMinutos = duracionMinutos,
            temaPrincipal = temaPrincipal,
            programasImplementados = programasImplementados,
            urlEvidencia = urlEvidencia,
            updatedAt = LocalDateTime.now(),
            isDeleted = false,
            isSynced = false
        )
        val erroresMap = validarDatosActividad(actividadToSave)
        if (erroresMap.isNotEmpty()) {
            _errores.value = erroresMap
            _mensaje.value = "Corrige los campos en rojo."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val institutionId = getCurrentInstitutionId() ?: run {
                _mensaje.value = "Error al guardar: No se ha seleccionado una institución."
                _isLoading.value = false
                return@launch
            }
            actividadToSave.usuarioInstitucionId = institutionId

            try {
                saveActividad(actividadToSave)
                _mensaje.value = "Actividad guardada correctamente."
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

    private fun validarDatosActividad(actvidad: Actividad): Map<String, String> {
        val erroresActuales = mutableMapOf<String, String>()

        if (actvidad.tipoActividadId <= 0) erroresActuales["tipoActividad"] = "Debes seleccionar un tipo de actividad."
        
        // Validar que la fecha no sea nula (esto no debería pasar si ya validamos el string, pero es una doble verificación)
        if (actvidad.fecha == null) {
            erroresActuales["fecha"] = "La fecha es obligatoria."
        } else if (actvidad.fecha.isAfter(LocalDate.now())) {
            erroresActuales["fecha"] = "La fecha no puede ser posterior a la fecha actual."
        }
        
        // La descripción ahora puede ser null, solo validamos si no está vacía cuando se proporciona
        if (actvidad.descripcionGeneral != null && actvidad.descripcionGeneral.isBlank()) {
            erroresActuales["descripcion"] = "La descripción no puede estar vacía si se proporciona."
        }
        
        // Validar límite de 255 caracteres para campos string (solo si no son null)
        actvidad.direccion?.let { if (it.length > 255) erroresActuales["direccion"] = "La dirección no puede exceder 255 caracteres." }
        actvidad.descripcionGeneral?.let { if (it.length > 255) erroresActuales["descripcion"] = "La descripción no puede exceder 255 caracteres." }
        actvidad.temaPrincipal?.let { if (it.length > 255) erroresActuales["temaPrincipal"] = "El tema principal no puede exceder 255 caracteres." }
        actvidad.programasImplementados?.let { if (it.length > 255) erroresActuales["programaImplementados"] = "Los programas implementados no pueden exceder 255 caracteres." }
        actvidad.urlEvidencia?.let { if (it.length > 255) erroresActuales["urlEvidencia"] = "La URL de evidencia no puede exceder 255 caracteres." }

        return erroresActuales
    }

    private fun obtenerCamposVisibles(nombreTipoActividad: String): Set<String> {
        return when (nombreTipoActividad) {
            "SESIÓN EDUCATIVA A NIVEL COMUNITARIO" -> setOf(
                "fecha", "direccion", "descripcion", "cantidadParticipantes", 
                "cantidadSesiones", "temaPrincipal", "urlEvidencia"
            )
            "SESIÓN EDUCATIVA A NIVEL DEL CENTRO DE SALUD" -> setOf(
                "fecha", "direccion", "descripcion", "cantidadParticipantes", 
                "cantidadSesiones", "temaPrincipal", "urlEvidencia"
            )
            "TALLER DE CAPACITACIÓN" -> setOf(
                "fecha", "descripcion", "cantidadParticipantes", "cantidadSesiones", 
                "duracionMinutos", "temaPrincipal", "urlEvidencia"
            )
            "ASESORÍA" -> setOf(
                "fecha", "direccion", "descripcion", "cantidadParticipantes", 
                "cantidadSesiones", "urlEvidencia"
            )
            "VISITA" -> setOf(
                "fecha", "direccion", "descripcion", "cantidadSesiones", "urlEvidencia"
            )
            "JORNADA" -> setOf(
                "fecha", "direccion", "descripcion", "cantidadParticipantes", 
                "cantidadSesiones", "urlEvidencia"
            )
            "CLUB" -> setOf(
                "fecha", "direccion", "descripcion", "cantidadParticipantes", 
                "cantidadSesiones", "temaPrincipal", "programaImplementados", "urlEvidencia"
            )
            "CARTELERA" -> setOf(
                "fecha", "descripcion", "cantidadSesiones"
            )
            "PROGRAMA \"SALUD VA A LA ESCUELA\"" -> setOf(
                "fecha", "direccion", "descripcion", "cantidadParticipantes", 
                "cantidadSesiones", "temaPrincipal", "programaImplementados", "urlEvidencia"
            )
            "EVENTO DE MEJORAMIENTO PROFESIONAL" -> setOf(
                "fecha", "descripcion", "cantidadParticipantes", "cantidadSesiones", 
                "temaPrincipal", "urlEvidencia"
            )
            "DISCUSIÓN DE CASOS CLÍNICOS" -> setOf(
                "fecha", "descripcion", "cantidadSesiones", "temaPrincipal"
            )
            "OTRA ACTIVIDAD" -> setOf(
                "fecha", "direccion", "descripcion", "cantidadParticipantes", 
                "cantidadSesiones", "duracionMinutos", "temaPrincipal", "programaImplementados", "urlEvidencia"
            )
            else -> emptySet()
        }
    }

}