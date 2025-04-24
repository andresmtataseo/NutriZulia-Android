package com.nutrizulia.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.Usuario
import com.nutrizulia.domain.usecase.GetUsuarioByCedulaClaveUseCase
import com.nutrizulia.domain.usecase.InsertUsuarioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getUsuarioByCedulaClaveUseCase: GetUsuarioByCedulaClaveUseCase,
    private val insertUsuarioUseCase: InsertUsuarioUseCase
) : ViewModel() {

    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> get() = _usuario

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> get() = _error

    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> get() = _salir

    private val _autenticado = MutableLiveData<Boolean>()
    val autenticado: LiveData<Boolean> get() = _autenticado

    fun logearUsuario(cedula: String, clave: String) {
        _error.value = false
        if (cedula.isNotBlank() || clave.isNotBlank()) {
            viewModelScope.launch {
                val result = getUsuarioByCedulaClaveUseCase(cedula, clave)
                if (result != null) {
                    _autenticado.value = true
                    _usuario.value = result
                } else {
                    _mensaje.value = "Datos incorrectos."
                    _error.value = true
                    _autenticado.value = false
                }
            }
        } else {
            _mensaje.value = "Completa los campos."
            _error.value = true
        }
    }

    fun crearUsuario() {
        val usuario = Usuario (
            id = 0,
            cedula = "30465183",
            primerNombre = "ANDRES",
            segundoNombre = "EDUARDO",
            primerApellido = "MORENO",
            segundoApellido = "TATASEO",
            profesion = "INGENIERO EN INFORMATICA",
            telefono = "04246719783",
            correo = "andresmoreno2001@gmail.com",
            clave = "andres",
            isActivo = true
        )
        viewModelScope.launch {
            val isExist = getUsuarioByCedulaClaveUseCase(usuario.cedula, usuario.clave)
            if (isExist != null) {
                Log.d("Usuario", "Ya existe")
                return@launch
            } else {
                val result = insertUsuarioUseCase(usuario)
                if ( result > 0 ) {
                    Log.d("Usuario", "Registrado exitosamente")
                }
                else {
                    Log.d("Usuario", "errorrrrr")
                }
            }

        }
    }

}