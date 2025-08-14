package com.nutrizulia.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.local.view.PerfilInstitucional
import com.nutrizulia.domain.usecase.catalog.SyncCatalog
import com.nutrizulia.domain.usecase.catalog.SyncCatalogsResult
import com.nutrizulia.domain.usecase.FullSyncCollections
import com.nutrizulia.domain.usecase.user.GetPerfilesInstitucionales
import com.nutrizulia.domain.usecase.user.GetPerfilesResult
import com.nutrizulia.domain.usecase.user.SyncResult
import com.nutrizulia.domain.usecase.user.SyncUsuarioInstituciones
import com.nutrizulia.util.SessionManager
import com.nutrizulia.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel orquestador para el proceso de precarga de datos.
 * Coordina la sincronización de catálogos, perfil de usuario y datos del usuario.
 * Actúa como director del flujo de sincronización completa al iniciar sesión.
 */
@HiltViewModel
class PreCargarViewModel @Inject constructor(
    private val syncCatalog: SyncCatalog,
    private val fullSyncCollections: FullSyncCollections,
    private val sessionManager: SessionManager,
    private val getPerfilesInstitucionales: GetPerfilesInstitucionales,
    private val tokenManager: TokenManager,
    private val syncUsuarioInstituciones: SyncUsuarioInstituciones
) : ViewModel() {

    // Estados del proceso de precarga
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
    
    // Estados del progreso de sincronización
    private val _syncProgress = MutableLiveData<SyncProgressInfo>()
    val syncProgress: LiveData<SyncProgressInfo> get() = _syncProgress
    
    /**
     * Información del progreso de sincronización
     */
    data class SyncProgressInfo(
        val phase: SyncPhase,
        val message: String,
        val progressPercentage: Int = 0,
        val details: String? = null
    )
    
    /**
     * Fases del proceso de sincronización
     */
    enum class SyncPhase {
        CATALOGS,
        USER_PROFILE,
        USER_DATA,
        INSTITUTIONAL_PROFILES,
        COMPLETED,
        ERROR
    }

    /**
     * Inicia el proceso completo de precarga de datos.
     * Orquesta la sincronización en el siguiente orden:
     * 1. Sincronización de catálogos
     * 2. Sincronización del perfil de usuario
     * 3. Sincronización de datos del usuario
     * 4. Obtención de perfiles institucionales
     */
    fun cargarDatos() {
        if (_isLoading.value == true) {
            Log.w("PreCargarViewModel", "Proceso de precarga ya en progreso, ignorando nueva solicitud")
            return
        }

        viewModelScope.launch {
            _isLoading.postValue(true)
            
            try {
                Log.d("PreCargarViewModel", "Iniciando proceso completo de precarga de datos")
                
                // Fase 1: Sincronización de catálogos
                if (!syncCatalogs()) {
                    return@launch // Error manejado en syncCatalogs()
                }
                
                // Fase 2: Sincronización del perfil de usuario
                if (!syncUserProfile()) {
                    return@launch // Error manejado en syncUserProfile()
                }
                
                // Fase 3: Sincronización de datos del usuario
                if (!syncUserData()) {
                    return@launch // Error manejado en syncUserData()
                }
                
                // Fase 4: Obtención de perfiles institucionales
                loadInstitutionalProfiles()
                
            } catch (e: Exception) {
                Log.e("PreCargarViewModel", "Error crítico en el proceso de precarga", e)
                updateSyncProgress(
                    SyncPhase.ERROR,
                    "Error crítico al sincronizar datos",
                    details = e.message
                )
                handleFailure("Error inesperado al sincronizar datos.", isAuthError = true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    
    /**
     * Fase 1: Sincronización de catálogos
     */
    private suspend fun syncCatalogs(): Boolean {
        updateSyncProgress(
            SyncPhase.CATALOGS,
            "Sincronizando catálogos...",
            progressPercentage = 10
        )
        
        return try {
            val result = syncCatalog()
            
            when (result) {
                is SyncCatalogsResult.Success -> {
                    Log.d("PreCargarViewModel", "Sincronización de catálogos completada exitosamente")
                    updateSyncProgress(
                        SyncPhase.CATALOGS,
                        "Catálogos sincronizados correctamente",
                        progressPercentage = 25
                    )
                    true
                }
                is SyncCatalogsResult.Failure -> {
                    Log.e("PreCargarViewModel", "Fallo en sincronización de catálogos: ${result.message}")
                    updateSyncProgress(
                        SyncPhase.ERROR,
                        "Error al sincronizar catálogos",
                        details = result.message
                    )
                    handleFailure("Error al sincronizar catálogos: ${result.message}")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e("PreCargarViewModel", "Excepción en sincronización de catálogos", e)
            updateSyncProgress(
                SyncPhase.ERROR,
                "Error crítico al sincronizar catálogos",
                details = e.message
            )
            handleFailure("Error crítico al sincronizar catálogos: ${e.message}")
            false
        }
    }
    
    /**
     * Fase 2: Sincronización del perfil de usuario
     */
    private suspend fun syncUserProfile(): Boolean {
        updateSyncProgress(
            SyncPhase.USER_PROFILE,
            "Sincronizando perfil de usuario...",
            progressPercentage = 35
        )
        
        return try {
            when (val userSyncResult = syncUsuarioInstituciones()) {
                is SyncResult.Success -> {
                    Log.d("PreCargarViewModel", "Sincronización de perfil de usuario completada")
                    updateSyncProgress(
                        SyncPhase.USER_PROFILE,
                        "Perfil de usuario sincronizado",
                        progressPercentage = 50
                    )
                    true
                }
                is SyncResult.Failure.NotAuthenticated,
                is SyncResult.Failure.InvalidToken -> {
                    Log.e("PreCargarViewModel", "Error de autenticación: ${userSyncResult.message}")
                    updateSyncProgress(
                        SyncPhase.ERROR,
                        "Error de autenticación",
                        details = userSyncResult.message
                    )
                    handleFailure(userSyncResult.message, isAuthError = true)
                    false
                }
                is SyncResult.Failure.ApiError -> {
                    Log.e("PreCargarViewModel", "Error de API en perfil de usuario: ${userSyncResult.message}")
                    updateSyncProgress(
                        SyncPhase.ERROR,
                        "Error al sincronizar perfil",
                        details = userSyncResult.message
                    )
                    handleFailure(userSyncResult.message)
                    false
                }
            }
        } catch (e: Exception) {
            Log.e("PreCargarViewModel", "Excepción en sincronización de perfil de usuario", e)
            updateSyncProgress(
                SyncPhase.ERROR,
                "Error crítico al sincronizar perfil",
                details = e.message
            )
            handleFailure("Error crítico al sincronizar perfil: ${e.message}")
            false
        }
    }
    
    /**
     * Fase 3: Sincronización de datos del usuario
     */
    private suspend fun syncUserData(): Boolean {
        updateSyncProgress(
            SyncPhase.USER_DATA,
            "Sincronizando datos del usuario...",
            progressPercentage = 60
        )
        
        return try {
            val result = fullSyncCollections.invoke { currentTable, currentIndex, totalTables ->
                val progress = 60 + (currentIndex * 20 / totalTables)
                updateSyncProgress(
                    SyncPhase.USER_DATA,
                    "Sincronizando $currentTable...",
                    progressPercentage = progress
                )
            }
            
            if (result.overallSuccess) {
                Log.d("PreCargarViewModel", "Sincronización de datos del usuario completada exitosamente")
                updateSyncProgress(
                    SyncPhase.USER_DATA,
                    "Datos del usuario sincronizados",
                    progressPercentage = 80
                )
                true
            } else {
                val failedTables = result.getTablesWithErrors()
                val errorMessage = failedTables.joinToString(", ") { it.tableName }
                
                // Verificar si fue éxito parcial (algunas tablas sincronizadas)
                if (result.tablesSuccessful > 0) {
                    Log.w("PreCargarViewModel", "Sincronización parcial de datos: $errorMessage")
                    updateSyncProgress(
                        SyncPhase.USER_DATA,
                        "Datos sincronizados parcialmente",
                        progressPercentage = 75,
                        details = "Algunas tablas no se sincronizaron correctamente: $errorMessage"
                    )
                    // Continuamos con éxito parcial
                    true
                } else {
                    Log.e("PreCargarViewModel", "Fallo completo en sincronización de datos: $errorMessage")
                    updateSyncProgress(
                        SyncPhase.ERROR,
                        "Error al sincronizar datos del usuario",
                        details = errorMessage
                    )
                    handleFailure("Error al sincronizar datos del usuario: $errorMessage")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e("PreCargarViewModel", "Excepción en sincronización de datos del usuario", e)
            updateSyncProgress(
                SyncPhase.ERROR,
                "Error crítico al sincronizar datos",
                details = e.message
            )
            handleFailure("Error crítico al sincronizar datos: ${e.message}")
            false
        }
    }
    
    /**
     * Fase 4: Carga de perfiles institucionales
     */
    private suspend fun loadInstitutionalProfiles() {
        updateSyncProgress(
            SyncPhase.INSTITUTIONAL_PROFILES,
            "Cargando perfiles institucionales...",
            progressPercentage = 90
        )
        
        try {
            when (val perfilesResult = getPerfilesInstitucionales()) {
                is GetPerfilesResult.Success -> {
                    val perfiles = perfilesResult.perfiles
                    _profiles.postValue(perfiles)
                    
                    if (perfiles.isEmpty()) {
                        Log.w("PreCargarViewModel", "Usuario sin instituciones asignadas")
                        updateSyncProgress(
                            SyncPhase.ERROR,
                            "Sin instituciones asignadas",
                            progressPercentage = 100,
                            details = "Contacte al administrador del sistema"
                        )
                        _mensaje.postValue("No tiene instituciones asignadas. Contacte al administrador.")
                        _salir.postValue(true)
                    } else {
                        Log.d("PreCargarViewModel", "Proceso de precarga completado exitosamente. ${perfiles.size} instituciones disponibles")
                        updateSyncProgress(
                            SyncPhase.COMPLETED,
                            "Sincronización completada",
                            progressPercentage = 100,
                            details = "${perfiles.size} instituciones disponibles"
                        )
                        _mensaje.postValue("Por favor, seleccione una institución para continuar.")
                    }
                }
                is GetPerfilesResult.Failure -> {
                    Log.e("PreCargarViewModel", "Error al obtener perfiles institucionales: ${perfilesResult.message}")
                    updateSyncProgress(
                        SyncPhase.ERROR,
                        "Error al cargar instituciones",
                        details = perfilesResult.message
                    )
                    handleFailure(perfilesResult.message, isAuthError = true)
                }
            }
        } catch (e: Exception) {
            Log.e("PreCargarViewModel", "Excepción al cargar perfiles institucionales", e)
            updateSyncProgress(
                SyncPhase.ERROR,
                "Error crítico al cargar instituciones",
                details = e.message
            )
            handleFailure("Error crítico al cargar instituciones: ${e.message}", isAuthError = true)
        }
    }
    
    /**
     * Actualiza el progreso de sincronización
     */
    private fun updateSyncProgress(
        phase: SyncPhase,
        message: String,
        progressPercentage: Int = 0,
        details: String? = null
    ) {
        val progressInfo = SyncProgressInfo(
            phase = phase,
            message = message,
            progressPercentage = progressPercentage,
            details = details
        )
        _syncProgress.postValue(progressInfo)
        _mensaje.postValue(message)
    }

    fun onInstitutionSelected(usuarioInstitucionId: Int) {
        viewModelScope.launch {
            sessionManager.saveCurrentInstitutionId(usuarioInstitucionId)
            _continuar.postValue(true)
        }
    }
    
    /**
     * Reinicia el proceso de sincronización
     */
    fun retrySyncProcess() {
        Log.d("PreCargarViewModel", "Reintentando proceso de sincronización")
        resetState()
        cargarDatos()
    }
    
    /**
     * Resetea todos los estados del ViewModel
     */
    fun resetState() {
        _mensaje.postValue("")
        _continuar.postValue(false)
        _salir.postValue(false)
        _profiles.postValue(emptyList())
        _authError.postValue(false)
        _syncProgress.postValue(
            SyncProgressInfo(
                phase = SyncPhase.CATALOGS,
                message = "Preparando sincronización...",
                progressPercentage = 0
            )
        )
        
        // Estados reseteados - los casos de uso no mantienen estado
    }
    
    /**
     * Obtiene el progreso actual como porcentaje
     */
    fun getCurrentProgressPercentage(): Int {
        return _syncProgress.value?.progressPercentage ?: 0
    }
    
    /**
     * Obtiene la fase actual de sincronización
     */
    fun getCurrentSyncPhase(): SyncPhase {
        return _syncProgress.value?.phase ?: SyncPhase.CATALOGS
    }
    
    /**
     * Verifica si el proceso de sincronización está completo
     */
    fun isSyncCompleted(): Boolean {
        return getCurrentSyncPhase() == SyncPhase.COMPLETED
    }
    
    /**
     * Verifica si hubo errores en la sincronización
     */
    fun hasSyncErrors(): Boolean {
        return getCurrentSyncPhase() == SyncPhase.ERROR
    }
    
    /**
     * Obtiene detalles del último error si existe
     */
    fun getLastErrorDetails(): String? {
        return _syncProgress.value?.details
    }
    
    // Los casos de uso se manejan directamente en este ViewModel orquestador

    private fun handleFailure(errorMessage: String, isAuthError: Boolean = false) {
        viewModelScope.launch {
            _mensaje.postValue(errorMessage)
            _salir.postValue(true)

            if (isAuthError) {
                tokenManager.clearToken()
                sessionManager.clearCurrentInstitution()
                _authError.postValue(true)
            }
        }
    }
}