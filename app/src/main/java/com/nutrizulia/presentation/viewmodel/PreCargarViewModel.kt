package com.nutrizulia.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.Usuario
import com.nutrizulia.domain.usecase.GetComunidades
import com.nutrizulia.domain.usecase.GetEntidades
import com.nutrizulia.domain.usecase.GetMunicipios
import com.nutrizulia.domain.usecase.GetParroquias
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PreCargarViewModel @Inject constructor(
    private val getEntidades: GetEntidades,
    private val getMunicipios: GetMunicipios,
    private val getParroquias: GetParroquias,
    private val getComunidades: GetComunidades
): ViewModel() {

    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> get() = _usuario

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje

    private val _continuar = MutableLiveData<Boolean>()
    val continuar: LiveData<Boolean> get() = _continuar

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun cargarDatos() {
        if (_isLoading.value == true) return

        viewModelScope.launch {
            _isLoading.value = true

            try { withContext(Dispatchers.Main) {
                    _mensaje.value = "Cargando Entidades..."
                }
                val entidades = getEntidades()

                for (entidad in entidades) {
                    withContext(Dispatchers.Main) {
                        _mensaje.value = "Cargando Municipios para ${entidad.entidad}..."
                    }
                    val municipios = getMunicipios(entidad.codEntidad)

                    for (municipio in municipios) {
                        withContext(Dispatchers.Main) {
                            _mensaje.value = "Cargando Parroquias para ${municipio.municipio}..."
                        }
                        val parroquias = getParroquias(entidad.codEntidad, municipio.codMunicipio)

                        for (parroquia in parroquias) {
                            withContext(Dispatchers.Main) {
                                _mensaje.value = "Cargando Comunidades para ${parroquia.parroquia}..."
                            }
                            getComunidades(entidad.codEntidad, municipio.codMunicipio, parroquia.codParroquia)
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    _mensaje.value = "Carga completa."
                    _continuar.value = true
                }

            } catch (e: Exception) {
                Log.e("PreCargarViewModel", "Error al cargar datos: ${e.message}")
                withContext(Dispatchers.Main) {
                    _mensaje.value = "Error: ${e.message}"
                    _continuar.value = false
                }
            }
        }
    }
}