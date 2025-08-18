package com.nutrizulia.presentation.viewmodel.representante

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.collection.PacienteRepresentado
import com.nutrizulia.domain.usecase.collection.GetPacientesRepresentadosByRepresentanteId
import com.nutrizulia.domain.usecase.user.GetCurrentInstitutionIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PacientesRepresentadosViewModel @Inject constructor(
    private val getPacientesRepresentadosByRepresentanteId: GetPacientesRepresentadosByRepresentanteId,
    private val getCurrentInstitutionId: GetCurrentInstitutionIdUseCase
) : ViewModel() {
    
    // Necesario
    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion
    
    private var _representanteId = MutableLiveData<String>()
    val representanteId: LiveData<String> get() = _representanteId
    
    // Búsqueda
    private val _pacientesRepresentados = MutableLiveData<List<PacienteRepresentado>>()
    val pacientesRepresentados: LiveData<List<PacienteRepresentado>> get() = _pacientesRepresentados
    
    private val _pacientesRepresentadosFiltrados = MutableLiveData<List<PacienteRepresentado>>()
    val pacientesRepresentadosFiltrados: LiveData<List<PacienteRepresentado>> get() = _pacientesRepresentadosFiltrados
    
    // UI states
    private val _filtro = MutableLiveData<String>()
    val filtro: LiveData<String> get() = _filtro
    
    private val _mensaje = MutableLiveData<String?>()
    val mensaje: LiveData<String?> get() = _mensaje
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    fun onCreate() {
        viewModelScope.launch {
            try {
                _idUsuarioInstitucion.value = getCurrentInstitutionId()
            } catch (e: Exception) {
                _mensaje.value = "Error al obtener la institución actual"
            }
        }
    }
    
    fun setRepresentanteId(representanteId: String) {
        _representanteId.value = representanteId
    }
    
    fun clearMensaje() {
        _mensaje.value = null
    }
    
    fun obtenerPacientesRepresentados() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val institutionId = getCurrentInstitutionId() ?: run {
                _mensaje.value = "No se ha seleccionado una institución."
                _isLoading.value = false
                return@launch
            }
            _idUsuarioInstitucion.value = institutionId
            
            val currentRepresentanteId = _representanteId.value ?: run {
                _mensaje.value = "No se ha seleccionado un representante."
                _isLoading.value = false
                return@launch
            }
            
            try {
                val result = getPacientesRepresentadosByRepresentanteId(institutionId, currentRepresentanteId)
                _pacientesRepresentados.value = result
                
                if (result.isEmpty()) {
                    _mensaje.value = "No se encontraron pacientes representados."
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al obtener pacientes representados: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun buscarPacientesRepresentados(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            if (query.isBlank()) {
                _filtro.value = ""
                _pacientesRepresentadosFiltrados.value = emptyList()
                _isLoading.value = false
                return@launch
            }
            
            val institutionId = getCurrentInstitutionId() ?: run {
                _mensaje.value = "No se ha seleccionado una institución."
                _isLoading.value = false
                return@launch
            }
            _idUsuarioInstitucion.value = institutionId
            
            val currentRepresentanteId = _representanteId.value ?: run {
                _mensaje.value = "No se ha seleccionado un representante."
                _isLoading.value = false
                return@launch
            }
            
            try {
                _filtro.value = query
                val result = getPacientesRepresentadosByRepresentanteId.withFilter(institutionId, currentRepresentanteId, query)
                
                if (result.isEmpty()) {
                    _pacientesRepresentadosFiltrados.value = emptyList()
                    _mensaje.value = "No se encontraron pacientes representados."
                } else {
                    _pacientesRepresentadosFiltrados.value = result
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al buscar pacientes representados: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}