package com.nutrizulia.presentation.viewmodel.usuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.usecase.user.GetMaxAppointmentsPerDayUseCase
import com.nutrizulia.domain.usecase.user.SaveMaxAppointmentsPerDayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditarDiasCitaViewModel @Inject constructor(
    private val getMaxAppointmentsPerDayUseCase: GetMaxAppointmentsPerDayUseCase,
    private val saveMaxAppointmentsPerDayUseCase: SaveMaxAppointmentsPerDayUseCase
) : ViewModel() {

    private val _maxAppointmentsPerDay = MutableStateFlow(8)
    val maxAppointmentsPerDay: StateFlow<Int> = _maxAppointmentsPerDay.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _exit = MutableStateFlow(false)
    val exit: StateFlow<Boolean> = _exit.asStateFlow()

    init {
        loadMaxAppointmentsPerDay()
    }

    private fun loadMaxAppointmentsPerDay() {
        viewModelScope.launch {
            getMaxAppointmentsPerDayUseCase().collect { maxAppointments ->
                _maxAppointmentsPerDay.value = maxAppointments
            }
        }
    }

    fun updateMaxAppointmentsPerDay(newValue: Int) {
        _maxAppointmentsPerDay.value = newValue
        _saveSuccess.value = false
        _errorMessage.value = null
    }

    fun saveMaxAppointmentsPerDay() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                saveMaxAppointmentsPerDayUseCase(_maxAppointmentsPerDay.value)
                
                _saveSuccess.value = true
                _exit.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al guardar la configuración"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSaveSuccess() {
        _saveSuccess.value = false
    }
}