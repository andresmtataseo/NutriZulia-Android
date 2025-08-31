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
                    val actividadJob = if (!actividadId.isNullOrBlank()) async { obtenerActividad(actividadId) } else null
                    catalogsJob?.await()
                    actividadJob?.await()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun obtenerActividad(actividadId: String) {
        val loadedActividad = getActividadById(actividadId, _idUsuarioInstitucion.value!!)
        if (loadedActividad == null) {
            _mensaje.postValue("No se encontró la actividad.")
            _salir.postValue(true)
            return
        }
        _actividad.postValue(loadedActividad)

        _selectedTipoActividad.postValue(getTipoActividadById(loadedActividad.tipoActividadId))
    }

    private suspend fun cargarCatalogos() {
        coroutineScope {
            val getTipoActividadesJob = async { getTipoActividades() }
            _tipoActividades.postValue(getTipoActividadesJob.await())
        }
    }

    fun onActividadSelected(tipoActividad: TipoActividad) { _selectedTipoActividad.value = tipoActividad }

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
        if (actvidad.fecha.isAfter(LocalDate.now())) erroresActuales["fecha"] = "La fecha no puede ser posterior a la fecha actual."

        return erroresActuales
    }

}