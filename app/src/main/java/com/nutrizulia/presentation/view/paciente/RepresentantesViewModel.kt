package com.nutrizulia.presentation.view.paciente

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
class RepresentantesViewModel @Inject constructor(
    private val getRepresentantesByFiltro: GetRepresentantesByFiltro,
    private val getRepresentantes: GetRepresentantes,
    private val getCurrentInstitutionId: GetCurrentInstitutionIdUseCase
) : ViewModel() {

    private val _representantes = MutableLiveData<List<Representante>>()
    val representantes: LiveData<List<Representante>> get() = _representantes

    private val _representantesFiltrados = MutableLiveData<List<Representante>>()
    val representantesFiltrados: LiveData<List<Representante>> get() = _representantesFiltrados

    private val _filtro = MutableLiveData<String>()
    val filtro: LiveData<String> get() = _filtro

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    fun onCreate() {
        obtenerPacientes()
    }

    fun obtenerPacientes() {
        viewModelScope.launch {
            _isLoading.value = true

            val institutionId = getCurrentInstitutionId() ?: run {
                _mensaje.value = "No se ha seleccionado una institución."
                return@launch
            }
            _idUsuarioInstitucion.value = institutionId

            val result = getRepresentantes(idUsuarioInstitucion.value ?: 0)
            if (result.isNotEmpty()) {
                _representantes.value = result
            }
            _isLoading.value = false
        }
    }

    fun buscarPacientes(query: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val institutionId = getCurrentInstitutionId() ?: run {
                _mensaje.value = "No se ha seleccionado una institución."
                return@launch
            }
            _idUsuarioInstitucion.value = institutionId

            _filtro.value = query
            val result = getRepresentantesByFiltro(idUsuarioInstitucion.value ?: 0, filtro.value ?: "")
            if (result.isNotEmpty()) {
                _representantesFiltrados.value = result
            } else {
                _mensaje.value = "No se encontraron representantes."
            }
            _isLoading.value = false
        }
    }

}
