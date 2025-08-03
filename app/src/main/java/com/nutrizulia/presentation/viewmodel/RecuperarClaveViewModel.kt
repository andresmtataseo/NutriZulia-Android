package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.ApiResponse
import com.nutrizulia.domain.usecase.auth.ForgotPasswordUseCase
import com.nutrizulia.util.CheckData.esCedulaValida
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecuperarClaveViewModel @Inject constructor(
    private val forgetPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message
    private val _apiResponse = MutableLiveData<ApiResponse<Any>>()
    val apiResponse: LiveData<ApiResponse<Any>> get() = _apiResponse

    fun recuperarClave(cedula: String) {
        val cedulaLimpia = cedula.trim().uppercase()

        if (esCedulaValida(cedulaLimpia)) {
            viewModelScope.launch {
                _loading.value = true
                try {
                    _apiResponse.value = forgetPasswordUseCase(cedulaLimpia)
                } catch (e: Exception) {
                    _message.value = e.message
                } finally {
                    _loading.value = false
                }
            }
        }


    }
}