package com.nutrizulia.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.usecase.auth.LogoutUseCase
import com.nutrizulia.domain.usecase.user.GetPerfilesInstitucionales
import com.nutrizulia.domain.usecase.user.GetPerfilesResult // ✅ 1. Importar el sealed class
import com.nutrizulia.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getPerfilesInstitucionales: GetPerfilesInstitucionales,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _logoutComplete = MutableLiveData<Boolean>()
    val logoutComplete: LiveData<Boolean> get() = _logoutComplete
    private val _isInstitutionSelected = MutableLiveData<Boolean>()
    val isInstitutionSelected: LiveData<Boolean> get() = _isInstitutionSelected

    fun onCreated() {
        checkIfInstitutionIsSelected()
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

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _logoutComplete.postValue(true)
        }
    }
}