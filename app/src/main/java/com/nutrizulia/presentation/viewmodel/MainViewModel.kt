package com.nutrizulia.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.usecase.auth.LogoutUseCase
import com.nutrizulia.domain.usecase.user.GetPerfilesInstitucionales
import com.nutrizulia.util.JwtUtils
import com.nutrizulia.util.SessionManager
import com.nutrizulia.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val usuarioInstitucion: GetPerfilesInstitucionales,
    private val sessionManager: SessionManager,
    private val tokenManager: TokenManager
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
            val institutionId = sessionManager.currentInstitutionIdFlow.firstOrNull()
            val token = tokenManager.getToken()
            val usuarioId = JwtUtils.extractIdUsuario(token)
            if (institutionId == null || institutionId <= 0) {
                _isInstitutionSelected.postValue(false)
                Log.d("MainViewModel", "No hay institución seleccionada")
            } else {
                val usuarioInstitucion = usuarioInstitucion(usuarioId ?: 0)
                Log.d("MainViewModel", "usuarioInstitucion: $usuarioInstitucion")
                if (usuarioInstitucion.isEmpty()) {
                    _isInstitutionSelected.postValue(false)
                    Log.d("MainViewModel", "No se encontraron datos del usuario en la institución")
                    return@launch
                }
                _isInstitutionSelected.postValue(true)
                Log.d("MainViewModel", " institución seleccionada")
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