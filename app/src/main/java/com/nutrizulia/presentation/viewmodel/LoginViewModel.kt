package com.nutrizulia.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.auth.SignIn
import com.nutrizulia.domain.usecase.auth.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _signInResult = MutableLiveData<Result<SignIn>>()
    val signInResult: LiveData<Result<SignIn>> get() = _signInResult

    fun logearUsuario(cedula: String, clave: String) {
        if (cedula.isNotBlank() && clave.isNotBlank()) {
            viewModelScope.launch {
                try {
                    Log.d("LoginViewModel", "Iniciando autenticación para Cédula: $cedula")

                    val result = signInUseCase(cedula, clave)
                    _signInResult.value = result

                    result.onSuccess { signIn ->
                        Log.d("LoginViewModel", "✅ Autenticación exitosa")
                        Log.d("LoginViewModel", "Token: ${signIn.token}")
                        Log.d("LoginViewModel", "Tipo de Token: ${signIn.type}")
                        Log.d("LoginViewModel", "Usuario: ${signIn.usuario.nombres} ${signIn.usuario.apellidos}")
                        Log.d("LoginViewModel", "Correo: ${signIn.usuario.correo}")
                    }

                    result.onFailure { error ->
                        Log.e("LoginViewModel", "❌ Fallo de autenticación: ${error.message}")
                    }

                } catch (e: Exception) {
                    Log.e("LoginViewModel", "❌ Excepción durante autenticación: ${e.message}")
                    _signInResult.value = Result.failure(e)
                }
            }
        } else {
            Log.w("LoginViewModel", "Campos vacíos: cedula='$cedula', clave='${clave.length} caracteres'")
            _signInResult.value = Result.failure(Exception("Completa los campos."))
        }
    }
}
