package com.nutrizulia.presentation.viewmodel.consulta

import androidx.lifecycle.*
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.enum.TipoConsulta
import com.nutrizulia.domain.model.catalog.Especialidad
import com.nutrizulia.domain.model.catalog.TipoActividad
import com.nutrizulia.domain.model.collection.Consulta
import com.nutrizulia.domain.usecase.catalog.GetEspecialidadById
import com.nutrizulia.domain.usecase.catalog.GetEspecialidades
import com.nutrizulia.domain.usecase.catalog.GetTipoActividadById
import com.nutrizulia.domain.usecase.catalog.GetTiposActividades
import com.nutrizulia.domain.usecase.collection.DetermineTipoConsulta
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InformacionGeneralViewModel @Inject constructor(
    private val getTiposActividades: GetTiposActividades,
    private val getEspecialidades: GetEspecialidades,
    private val getTipoActividad: GetTipoActividadById,
    private val getEspecialidad: GetEspecialidadById,
    private val determineTipoConsulta: DetermineTipoConsulta
) : ViewModel() {

    private var isDataLoaded: Boolean = false
    private var originalConsultaState: Consulta? = null

    private val _tiposActividades = MutableLiveData<List<TipoActividad>>()
    val tiposActividades: LiveData<List<TipoActividad>> = _tiposActividades

    private val _especialidades = MutableLiveData<List<Especialidad>>()
    val especialidades: LiveData<List<Especialidad>> = _especialidades

    private val _tiposConsultas = MutableLiveData<List<TipoConsulta>>()
    val tiposConsultas: LiveData<List<TipoConsulta>> = _tiposConsultas

    private val _selectedTipoActividad = MutableLiveData<TipoActividad?>()
    val selectedTipoActividad: LiveData<TipoActividad?> = _selectedTipoActividad

    private val _selectedEspecialidad = MutableLiveData<Especialidad?>()
    val selectedEspecialidad: LiveData<Especialidad?> = _selectedEspecialidad

    private val _selectedTipoConsulta = MutableLiveData<TipoConsulta?>()
    val selectedTipoConsulta: LiveData<TipoConsulta?> = _selectedTipoConsulta

    private val _errores = MutableLiveData<Map<String, String>>()
    val errores: LiveData<Map<String, String>> = _errores

    fun loadInitialData(
        pacienteId: String,
        isEditable: Boolean,
        consultaPrevia: Consulta?, // Este parámetro es la clave
        consultaOriginal: Consulta?
    ) {
        if (isDataLoaded) {
            return
        }

        originalConsultaState = consultaOriginal

        if (consultaPrevia != null) {
            _selectedTipoConsulta.value = consultaPrevia.tipoConsulta
        } else {
            executeDetermineTipoConsulta(pacienteId)
        }

        if (isEditable) {
            viewModelScope.launch {
                _tiposActividades.value = getTiposActividades()
                _especialidades.value = getEspecialidades()
            }
        }

        consultaPrevia?.let {
            loadSelectionsFromConsulta(it)
        }

        isDataLoaded = true
    }

    private fun executeDetermineTipoConsulta(pacienteId: String) {
        viewModelScope.launch {
            val tipoConsulta: TipoConsulta = determineTipoConsulta(pacienteId)
            _selectedTipoConsulta.value = tipoConsulta
        }
    }

    private fun loadSelectionsFromConsulta(consulta: Consulta) {
        viewModelScope.launch {
            consulta.tipoActividadId.let { id -> _selectedTipoActividad.value = getTipoActividad(id) }
            consulta.especialidadRemitenteId.let { id -> _selectedEspecialidad.value = getEspecialidad(id) }
            _selectedTipoConsulta.value = consulta.tipoConsulta
        }
    }

    fun areFieldsEditable(consultaPrevia: Consulta?): Boolean {
        return when (consultaPrevia?.estado) {
            Estado.PENDIENTE, Estado.REPROGRAMADA -> false
            else -> true
        }
    }

    fun restoreOriginalData() {
        originalConsultaState?.let {
            loadSelectionsFromConsulta(it)
        }
    }

    fun selectTipoActividad(item: TipoActividad) { _selectedTipoActividad.value = item }
    fun selectEspecialidad(item: Especialidad) { _selectedEspecialidad.value = item }

    fun clearSelections() {
        _selectedTipoActividad.value = null
        _selectedEspecialidad.value = null
    }

    fun validateAndPrepareData(): Boolean {
        val currentErrors = mutableMapOf<String, String>()
        if (_selectedTipoActividad.value == null) currentErrors["tipoActividad"] = "Selecciona un tipo de actividad"
        if (_selectedEspecialidad.value == null) currentErrors["especialidad"] = "Selecciona una especialidad"
        if (_selectedTipoConsulta.value == null) currentErrors["tipoConsulta"] = "Selecciona un tipo de consulta"

        _errores.value = currentErrors
        return currentErrors.isEmpty()
    }
}