package com.nutrizulia.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.local.view.PerfilInstitucional
import com.nutrizulia.domain.usecase.catalog.SyncCatalog
import com.nutrizulia.domain.usecase.user.GetPerfilesInstitucionales
import com.nutrizulia.domain.usecase.user.syncUsuarioIntituciones
import com.nutrizulia.util.JwtUtils
import com.nutrizulia.util.SessionManager
import com.nutrizulia.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreCargarViewModel @Inject constructor(
    private val syncCatalog: SyncCatalog,
    private val sessionManager: SessionManager,
    private val getPerfilesInstitucionales: GetPerfilesInstitucionales,
    private val tokenManager: TokenManager,
    private val syncUsuarioIntituciones: syncUsuarioIntituciones
) : ViewModel() {

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje

    private val _continuar = MutableLiveData<Boolean>()
    val continuar: LiveData<Boolean> get() = _continuar

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _profiles = MutableLiveData<List<PerfilInstitucional>>()
    val profiles: LiveData<List<PerfilInstitucional>> get() = _profiles

    // ✅ 1. Nuevo LiveData para notificar errores de autenticación/sesión
    private val _authError = MutableLiveData<Boolean>()
    val authError: LiveData<Boolean> get() = _authError

    fun cargarDatos() {
        if (_isLoading.value == true) return

        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                // ✅ 2. Principio Fail-Fast: Validar el token y el usuario primero
                val token = tokenManager.getToken()
                if (token.isNullOrEmpty()) {
                    handleAuthError("Error: Sesión no encontrada. Por favor, inicie sesión de nuevo.")
                    return@launch
                }

                val usuarioId = JwtUtils.extractIdUsuario(token)
                if (usuarioId == null || usuarioId == 0) {
                    handleAuthError("Error: Token de sesión inválido.")
                    return@launch
                }

                // --- Si las validaciones pasan, continuamos con la carga ---
                _mensaje.postValue("Sincronizando catálogos...")
                syncCatalog()

                _mensaje.postValue("Sincronizando perfil de usuario...")
                syncUsuarioIntituciones(usuarioId)

                val perfiles = getPerfilesInstitucionales(usuarioId)
                _profiles.postValue(perfiles)

                if (perfiles.isEmpty()) {
                    _mensaje.postValue("No tiene instituciones asignadas. Contacte al administrador.")
                    // Nota: No consideramos esto un error crítico, así que no cerramos la sesión.
                } else {
                    _mensaje.postValue("Por favor, seleccione una institución para continuar.")
                }

            } catch (e: Exception) {
                Log.e("PreCargarViewModel", "Error al cargar datos: ${e.message}")
                // ✅ 3. Cualquier excepción en la carga se considera un error crítico
                handleAuthError("Error al sincronizar datos: ${e.message}")
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

    // ✅ 4. Nueva función centralizada para manejar errores críticos
    private fun handleAuthError(errorMessage: String) {
        viewModelScope.launch {
            _mensaje.postValue(errorMessage)
            // Limpiamos la sesión y el token
            tokenManager.clearToken()
            sessionManager.clearCurrentInstitution()
            // Notificamos a la Activity que debe cerrarse
            _authError.postValue(true)
        }
    }
}