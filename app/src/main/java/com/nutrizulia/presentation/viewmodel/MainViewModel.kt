package com.nutrizulia.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.usecase.auth.LogoutUseCase
import com.nutrizulia.domain.usecase.user.GetPerfilesInstitucionales
import com.nutrizulia.domain.usecase.user.GetPerfilesResult // ✅ 1. Importar el sealed class
import com.nutrizulia.domain.usecase.user.GetUserDetails
import com.nutrizulia.util.SessionManager
import com.nutrizulia.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getPerfilesInstitucionales: GetPerfilesInstitucionales,
    private val getUserDetails: GetUserDetails,
    private val sessionManager: SessionManager,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _logoutComplete = MutableLiveData<Boolean>()
    val logoutComplete: LiveData<Boolean> get() = _logoutComplete
    private val _isInstitutionSelected = MutableLiveData<Boolean>()
    val isInstitutionSelected: LiveData<Boolean> get() = _isInstitutionSelected
    
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName
    private val _institutionName = MutableLiveData<String>()
    val institutionName: LiveData<String> get() = _institutionName

    fun onCreated() {
        checkIfInstitutionIsSelected()
        loadHeaderData()
        observeInstitutionChanges()
    }

    private fun checkIfInstitutionIsSelected() {
        viewModelScope.launch {
            val selectedInstitutionId = sessionManager.currentInstitutionIdFlow.firstOrNull()

            if (selectedInstitutionId == null || selectedInstitutionId <= 0) {
                _isInstitutionSelected.postValue(false)
                Log.d("MainViewModel", "No hay institución guardada en sesión.")
                return@launch
            }

            when (val result = getPerfilesInstitucionales()) {
                is GetPerfilesResult.Success -> {
                    val isValidSelection = result.perfiles.any { perfil ->
                        perfil.usuarioInstitucionId == selectedInstitutionId
                    }

                    if (isValidSelection) {
                        _isInstitutionSelected.postValue(true)
                        Log.d("MainViewModel", "Institución seleccionada ($selectedInstitutionId) es válida.")
                    } else {
                        _isInstitutionSelected.postValue(false)
                        Log.w("MainViewModel", "La institución guardada ($selectedInstitutionId) ya no es válida para el usuario.")
                        // Opcional: limpiar la selección inválida de la sesión
                        sessionManager.clearCurrentInstitution()
                    }
                }
                is GetPerfilesResult.Failure -> {
                    // Si falla (token inválido/ausente), la sesión está corrupta.
                    Log.e("MainViewModel", "Error de sesión al verificar perfiles: ${result.message}")
                    _isInstitutionSelected.postValue(false)
                }
            }
        }
    }

    private fun loadHeaderData() {
        viewModelScope.launch {
            try {
                // Evitar cargar si no hay token (usuario desconectado)
                val token = tokenManager.getToken()
                if (token.isNullOrEmpty()) {
                    Log.d("MainViewModel", "Token vacío; omitimos carga de header.")
                    return@launch
                }

                when (val result = getPerfilesInstitucionales()) {
                    is GetPerfilesResult.Success -> {
                        val currentInstitutionId = sessionManager.currentInstitutionIdFlow.firstOrNull()
                        val currentProfile = result.perfiles.find { 
                            it.usuarioInstitucionId == currentInstitutionId 
                        }
                        
                        if (currentProfile != null) {
                            val usuario = getUserDetails(currentProfile.usuarioId)
                            if (usuario != null) {
                                val fullName = usuario.nombres.substringBefore(" ") + " " + usuario.apellidos.substringBefore(" ")
                                _userName.postValue(fullName)
                            }
                            _institutionName.postValue(currentProfile.institucionNombre)
                        }
                    }
                    is GetPerfilesResult.Failure -> {
                        Log.e("MainViewModel", "Error al cargar datos del header: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error inesperado al cargar datos del header: ${e.message}", e)
            }
        }
    }

    private fun observeInstitutionChanges() {
        viewModelScope.launch {
            sessionManager.currentInstitutionIdFlow.collect { institutionId ->
                if (institutionId != null && institutionId > 0) {
                    loadHeaderData()
                }
            }
        }
    }

    fun refreshHeaderData() {
        loadHeaderData()
    }

    fun logout() {
        // Notificar inmediatamente al UI y limpiar local
        _logoutComplete.postValue(true)
        viewModelScope.launch {
            try {
                tokenManager.clearToken()
            } catch (e: Exception) {
                Log.w("MainViewModel", "No se pudo limpiar token local: ${e.message}", e)
            }

            try {
                sessionManager.clearCurrentInstitution()
            } catch (e: Exception) {
                Log.w("MainViewModel", "No se pudo limpiar institución local: ${e.message}", e)
            }

            // Ejecutar logout del servidor en segundo plano (no bloquea la UI)
            try {
                logoutUseCase()
                Log.d("MainViewModel", "Logout en servidor ejecutado.")
            } catch (e: Exception) {
                Log.w("MainViewModel", "Fallo logout en servidor; UI ya desconectada: ${e.message}")
            }
        }
    }
}