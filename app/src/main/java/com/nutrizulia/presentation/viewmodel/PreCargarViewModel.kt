package com.nutrizulia.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.local.view.PerfilInstitucional
import com.nutrizulia.domain.usecase.catalog.SyncCatalog
import com.nutrizulia.domain.usecase.catalog.SyncCatalogsResult
import com.nutrizulia.domain.usecase.user.GetPerfilesInstitucionales
import com.nutrizulia.domain.usecase.user.GetPerfilesResult
import com.nutrizulia.domain.usecase.user.SyncResult
import com.nutrizulia.domain.usecase.user.SyncUsuarioInstituciones
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
    private val syncUsuarioInstituciones: SyncUsuarioInstituciones
) : ViewModel() {

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje
    private val _continuar = MutableLiveData<Boolean>()
    val continuar: LiveData<Boolean> get() = _continuar
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> get() = _salir
    private val _profiles = MutableLiveData<List<PerfilInstitucional>>()
    val profiles: LiveData<List<PerfilInstitucional>> get() = _profiles
    private val _authError = MutableLiveData<Boolean>()
    val authError: LiveData<Boolean> get() = _authError

    fun cargarDatos() {
        if (_isLoading.value == true) return

        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                _mensaje.postValue("Sincronizando catálogos...")
                when (val catalogResult = syncCatalog()) {
                    is SyncCatalogsResult.Success -> {
                        _mensaje.postValue("Sincronizando perfil de usuario...")
                        when (val userSyncResult = syncUsuarioInstituciones()) {
                            is SyncResult.Success -> {
                                when (val perfilesResult = getPerfilesInstitucionales()) {
                                    is GetPerfilesResult.Success -> {
                                        val perfiles = perfilesResult.perfiles
                                        _profiles.postValue(perfiles)

                                        if (perfiles.isEmpty()) {
                                            _mensaje.postValue("No tiene instituciones asignadas. Contacte al administrador.")
                                            _salir.postValue(true)
                                        } else {
                                            _mensaje.postValue("Por favor, seleccione una institución para continuar.")
                                        }
                                    }
                                    is GetPerfilesResult.Failure -> {
                                        handleFailure(perfilesResult.message, isAuthError = true)
                                    }
                                }
                            }
                            is SyncResult.Failure.NotAuthenticated,
                            is SyncResult.Failure.InvalidToken -> {
                                handleFailure(userSyncResult.message, isAuthError = true)
                            }
                            is SyncResult.Failure.ApiError -> {
                                Log.e("PreCargarViewModel", "User sync API error: ${userSyncResult.message}")
                                handleFailure(userSyncResult.message)
                            }
                        }
                    }
                    is SyncCatalogsResult.Failure -> {
                        Log.e("PreCargarViewModel", "Catalog sync failed. Details: ${catalogResult.details}")
                        handleFailure(catalogResult.message)
                    }
                }
            } catch (e: Exception) {
                Log.e("PreCargarViewModel", "Error crítico al cargar datos: ${e.message}", e)
                handleFailure("Error inesperado al sincronizar datos.", isAuthError = true)
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

    /**
     * Centraliza el manejo de todos los errores que deben detener el flujo.
     * @param errorMessage El mensaje a mostrar al usuario.
     * @param isAuthError Indica si el error invalida la sesión actual.
     */
    private fun handleFailure(errorMessage: String, isAuthError: Boolean = false) {
        viewModelScope.launch {
            _mensaje.postValue(errorMessage)
            _salir.postValue(true) // ✅ Siempre activa la salida en caso de error

            if (isAuthError) {
                tokenManager.clearToken()
                sessionManager.clearCurrentInstitution()
                _authError.postValue(true)
            }
        }
    }
}