package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.auth.SignIn
import com.nutrizulia.domain.usecase.auth.SignInUseCase
import com.nutrizulia.util.CheckData.esCedulaValida
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.nutrizulia.data.remote.api.ApiHttpException

// Manejo de errores específico para la UI de login
sealed class SignInError {
    data class Forbidden(val message: String) : SignInError()
    data class Other(val message: String) : SignInError()
    data class InvalidInput(val message: String) : SignInError()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _signInResult = MutableLiveData<Result<SignIn>>()
    val signInResult: LiveData<Result<SignIn>> get() = _signInResult
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    // LiveData para errores diferenciados
    private val _signInError = MutableLiveData<SignInError>()
    val signInError: LiveData<SignInError> get() = _signInError

    fun logearUsuario(cedula: String, clave: String) {
        val cedulaLimpia = cedula.trim().uppercase()

        if (esCedulaValida(cedulaLimpia).not() || clave.isEmpty()) {
            _signInError.value = SignInError.InvalidInput("Completa los campos correctamente.")
            return
        }

        viewModelScope.launch {
            _loading.value = true
            val result = signInUseCase(cedulaLimpia, clave)
            _loading.value = false

            if (result.isSuccess) {
                _signInResult.value = result
            } else {
                val ex = result.exceptionOrNull()
                val message = ex?.message ?: "Error desconocido"

                when (ex) {
                    is ApiHttpException -> {
                        if (ex.statusCode == 401) {
                            _signInError.value = SignInError.Forbidden(message)
                        } else {
                            _signInError.value = SignInError.Other(message)
                        }
                    }
                    else -> {
                        _signInError.value = SignInError.Other(message)
                    }
                }
            }
        }
    }
}
