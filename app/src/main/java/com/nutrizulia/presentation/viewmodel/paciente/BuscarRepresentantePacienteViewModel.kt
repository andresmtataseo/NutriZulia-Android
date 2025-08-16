package com.nutrizulia.presentation.viewmodel.paciente

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.collection.Representante
import com.nutrizulia.domain.usecase.collection.GetRepresentantes
import com.nutrizulia.domain.usecase.collection.GetRepresentantesByFiltro
import com.nutrizulia.domain.usecase.user.GetCurrentInstitutionIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuscarRepresentantePacienteViewModel @Inject constructor(
    private val getRepresentantesByFiltro: GetRepresentantesByFiltro,
    private val getRepresentantes: GetRepresentantes,
    private val getCurrentInstitutionId: GetCurrentInstitutionIdUseCase
) : ViewModel() {
    // Necesario
    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion
    // Busqueda
    private val _representantes = MutableLiveData<List<Representante>>()
    val representantes: LiveData<List<Representante>> get() = _representantes
    private val _representantesFiltrados = MutableLiveData<List<Representante>>()
    val representantesFiltrados: LiveData<List<Representante>> get() = _representantesFiltrados

    // Ui states
    private val _filtro = MutableLiveData<String>()
    val filtro: LiveData<String> get() = _filtro
    private val _mensaje = MutableLiveData<String?>()
    val mensaje: LiveData<String?> get() = _mensaje
    private val _errores = MutableLiveData<Map<String, String>>()
    val errores: LiveData<Map<String, String>> = _errores
    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> = _salir
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun onCreate() {
        obtenerRepresentantes()
    }

    fun clearMensaje() {
        _mensaje.value = null
    }

    fun obtenerRepresentantes() {
        viewModelScope.launch {
            _isLoading.value = true

            val institutionId = getCurrentInstitutionId() ?: run {
                _mensaje.value = "No se ha seleccionado una institución."
                _isLoading.value = false
                return@launch
            }
            _idUsuarioInstitucion.value = institutionId

            val result = getRepresentantes(idUsuarioInstitucion.value ?: 0)
            _representantes.value = result
            _isLoading.value = false
        }
    }

    fun buscarRepresentantes(query: String) {
        viewModelScope.launch {
            _isLoading.value = true

            if (query.isBlank()) {
                _filtro.value = ""
                _representantesFiltrados.value = emptyList()
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
            val result = getRepresentantesByFiltro(idUsuarioInstitucion.value ?: 0, filtro.value ?: "")
            if (result.isEmpty()) {
                _representantesFiltrados.value = emptyList()
                _mensaje.value = "No se encontraron representantes."
            } else {
                _representantesFiltrados.value = result
            }
            _isLoading.value = false
        }
    }

}