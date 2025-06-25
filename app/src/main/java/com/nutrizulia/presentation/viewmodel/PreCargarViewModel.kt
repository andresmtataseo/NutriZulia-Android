package com.nutrizulia.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.usecase.catalog.SyncCatalog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PreCargarViewModel @Inject constructor(
    private val syncCatalog: SyncCatalog
) : ViewModel() {

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

            try {
                withContext(Dispatchers.Main) {
                    _mensaje.value = "Sincronizando catálogos..."
                }

                val resultado = syncCatalog()

                withContext(Dispatchers.Main) {
                    if (resultado.success) {
                        _mensaje.value = "Catálogos sincronizados (${resultado.totalInsertados})"
                        _continuar.value = true
                    } else {
                        _mensaje.value = "Error: ${resultado.message}"
                        _continuar.value = false
                    }
                }

            } catch (e: Exception) {
                Log.e("PreCargarViewModel", "Error al cargar datos: ${e.message}")
                withContext(Dispatchers.Main) {
                    _mensaje.value = "Error: ${e.message}"
                    _continuar.value = false
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}
