package com.nutrizulia.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.local.view.PerfilInstitucional
import com.nutrizulia.domain.usecase.user.GetPerfilesInstitucionales
import com.nutrizulia.util.JwtUtils
import com.nutrizulia.util.SessionManager
import com.nutrizulia.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeleccionarInstitucionViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val getPerfilesInstitucionales: GetPerfilesInstitucionales,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje

    private val _continuar = MutableLiveData(false)
    val continuar: LiveData<Boolean> get() = _continuar

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _perfilInstitucional = MutableLiveData<List<PerfilInstitucional>>()
    val perfilInstitucional: LiveData<List<PerfilInstitucional>> get() = _perfilInstitucional

    fun onCreate() {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val token = tokenManager.getToken()
                if (token.isNullOrEmpty()) {
                    _mensaje.postValue("Error de autenticación. Por favor, inicia sesión de nuevo.")
                    // Aquí podrías tener un LiveData para navegar al login, ej: _navigateToLogin.postValue(true)
                    return@launch // El finally se ejecutará
                }

                val usuarioId = JwtUtils.extractIdUsuario(token)
                if (usuarioId == null || usuarioId == 0) {
                    _mensaje.postValue("No se pudo verificar tu identidad. Intenta iniciar sesión de nuevo.")
                    return@launch // El finally se ejecutará
                }

                val perfiles = getPerfilesInstitucionales(usuarioId)
                _perfilInstitucional.postValue(perfiles)

                if (perfiles.isEmpty()) {
                    _mensaje.postValue("No tiene instituciones asignadas. Contacte al administrador.")
                }

            } catch (e: Exception) { // Captura genérica para otros errores
                Log.e("SeleccionarInstitucionViewModel", "Error inesperado al cargar datos: ${e.message}")
                _mensaje.postValue("Ocurrió un error inesperado al cargar la información. Inténtalo más tarde.")
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