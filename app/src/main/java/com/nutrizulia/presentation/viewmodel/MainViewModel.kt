package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.usecase.auth.LogoutUseCase
import com.nutrizulia.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _logoutComplete = MutableLiveData<Boolean>()
    val logoutComplete: LiveData<Boolean> get() = _logoutComplete
    private val _isInstitutionSelected = MutableLiveData<Boolean>()
    val isInstitutionSelected: LiveData<Boolean> get() = _isInstitutionSelected

    init {
        checkIfInstitutionIsSelected()
    }

    private fun checkIfInstitutionIsSelected() {
        viewModelScope.launch {
            val institutionId = sessionManager.currentInstitutionIdFlow.firstOrNull()
            if (institutionId == null || institutionId <= 0) {
                _isInstitutionSelected.postValue(false)
            } else {
                _isInstitutionSelected.postValue(true)
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