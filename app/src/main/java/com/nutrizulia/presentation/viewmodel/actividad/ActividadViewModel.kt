package com.nutrizulia.presentation.viewmodel.actividad

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.local.view.ActividadConTipo
import com.nutrizulia.domain.model.collection.Actividad
import com.nutrizulia.domain.usecase.collection.GetActividades
import com.nutrizulia.domain.usecase.collection.GetActividadesByFiltro
import com.nutrizulia.domain.usecase.user.GetCurrentInstitutionIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActividadViewModel @Inject constructor(
    private val getActividades: GetActividades,
    private val getActividadesByFiltro: GetActividadesByFiltro,
    private val getCurrentInstitutionId: GetCurrentInstitutionIdUseCase
): ViewModel() {

    private val _actividades = MutableLiveData<List<ActividadConTipo>>()
    val actividades: LiveData<List<ActividadConTipo>> get() = _actividades

    private val _actividadesFiltradas = MutableLiveData<List<ActividadConTipo>>()
    val actividadesFiltradas: LiveData<List<ActividadConTipo>> get() = _actividadesFiltradas

    private val _filtro = MutableLiveData<String>()
    val filtro: LiveData<String> get() = _filtro

    private val _mensaje = MutableLiveData<String?>()
    val mensaje: LiveData<String?> get() = _mensaje

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    fun onCreate() {
        obtenerActividades()
    }

    fun clearMensaje() {
        _mensaje.value = null
    }

    fun obtenerActividades() {
        viewModelScope.launch {
            _isLoading.value = true
            val institutionId = getCurrentInstitutionId() ?: run {
                _mensaje.value = "No se ha seleccionado una institución."
                _isLoading.value = false
                return@launch
            }
            _idUsuarioInstitucion.value = institutionId
            val result = getActividades(idUsuarioInstitucion.value!!)
            _actividades.value = result
            _isLoading.value = false
        }
    }

    fun buscarActividades(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            if (query.isBlank()) {
                _filtro.value = ""
                _actividadesFiltradas.value = emptyList()
                _isLoading.value = false
                return@launch
            }
            val institutionId = getCurrentInstitutionId() ?: run {
                _mensaje.value = "No se ha seleccionado una institución."
                _isLoading.value = false
                return@launch
            }
            _idUsuarioInstitucion.value = institutionId
            _filtro.value = query
            val result = getActividadesByFiltro(idUsuarioInstitucion.value!!, filtro.value ?: "")
            if (result.isEmpty()) {
                _actividadesFiltradas.value = emptyList()
                _mensaje.value = "No se encontraron actividades."
            } else {
                _actividadesFiltradas.value = result
            }
            _isLoading.value = false
        }
    }
}