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

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _signInResult = MutableLiveData<Result<SignIn>>()
    val signInResult: LiveData<Result<SignIn>> get() = _signInResult
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun logearUsuario(cedula: String, clave: String) {
        val cedulaLimpia = cedula.trim().uppercase()

        if (esCedulaValida(cedulaLimpia).not() || clave.isEmpty()) {
            _signInResult.value = Result.failure(Exception("Completa los campos correctamente."))
            return
        }

        viewModelScope.launch {
            _loading.value = true
            val result = signInUseCase(cedulaLimpia, clave)
            _loading.value = false
            _signInResult.value = result
        }
    }
}
