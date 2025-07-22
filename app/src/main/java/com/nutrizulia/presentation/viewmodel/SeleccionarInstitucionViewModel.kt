package com.nutrizulia.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.local.view.PerfilInstitucional
import com.nutrizulia.domain.usecase.user.GetPerfilesInstitucionales
import com.nutrizulia.domain.usecase.user.GetPerfilesResult // ✅ 1. Importar el sealed class
import com.nutrizulia.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeleccionarInstitucionViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val getPerfilesInstitucionales: GetPerfilesInstitucionales
) : ViewModel() {

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje

    private val _continuar = MutableLiveData(false)
    val continuar: LiveData<Boolean> get() = _continuar

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _perfilInstitucional = MutableLiveData<List<PerfilInstitucional>>()
    val perfilInstitucional: LiveData<List<PerfilInstitucional>> get() = _perfilInstitucional

    // ✅ Opcional pero recomendado: para manejar errores de sesión.
    private val _authError = MutableLiveData<Boolean>()
    val authError: LiveData<Boolean> get() = _authError

    fun onCreate() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // ✅ 2. Llamar al caso de uso sin parámetros y manejar su resultado.
                when (val result = getPerfilesInstitucionales()) {
                    is GetPerfilesResult.Success -> {
                        val perfiles = result.perfiles
                        _perfilInstitucional.postValue(perfiles)

                        if (perfiles.isEmpty()) {
                            _mensaje.postValue("No tiene instituciones asignadas. Contacte al administrador.")
                        }
                    }
                    is GetPerfilesResult.Failure -> {
                        // ✅ 3. Manejar errores de sesión de forma centralizada.
                        Log.e("SeleccionarInstitucionViewModel", "Error de sesión: ${result.message}")
                        _mensaje.postValue(result.message)
                        _authError.postValue(true) // Notificar a la UI para que navegue al login.
                    }
                }
            } catch (e: Exception) {
                Log.e("SeleccionarInstitucionViewModel", "Error inesperado: ${e.message}", e)
                _mensaje.postValue("Ocurrió un error inesperado al cargar la información.")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun onInstitutionSelected(usuarioInstitucionId: Int) {
        viewModelScope.launch {
            sessionManager.saveCurrentInstitutionId(usuarioInstitucionId)
            _continuar.postValue(true)
        }
    }
}