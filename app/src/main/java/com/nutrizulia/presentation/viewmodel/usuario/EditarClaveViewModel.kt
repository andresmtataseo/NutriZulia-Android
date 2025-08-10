package com.nutrizulia.presentation.viewmodel.usuario

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.user.Usuario
import com.nutrizulia.domain.usecase.auth.ChangePasswordUseCase
import com.nutrizulia.util.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditarClaveViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _usuario = MutableLiveData<Usuario>()
    val usuario: LiveData<Usuario> = _usuario

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errores = MutableLiveData<Map<String, String>>()
    val errores: LiveData<Map<String, String>> = _errores

    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> get() = _salir

    fun onCreate() {
        _isLoading.value = false
        _errores.value = emptyMap()
        _mensaje.value = ""
        _salir.value = false
    }

    fun cambiarClave(claveActual: String, claveNueva: String, claveNuevaConfirmacion: String) {
        // Limpiar errores previos
        _errores.value = emptyMap()
        _mensaje.value = ""

        // Validaciones locales
        val erroresValidacion = mutableMapOf<String, String>()

        if (claveActual.isBlank()) {
            erroresValidacion["contraseña"] = "La contraseña actual es requerida"
        }

        if (claveNueva.isBlank()) {
            erroresValidacion["contraseñaNueva"] = "La nueva contraseña es requerida"
        } else if (!Utils.esClaveValida(claveNueva)) {
            erroresValidacion["contraseñaNueva"] = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
        }

        if (claveNuevaConfirmacion.isBlank()) {
            erroresValidacion["contraseñaNuevaConfirmacion"] = "Debe confirmar la nueva contraseña"
        } else if (claveNueva != claveNuevaConfirmacion) {
            erroresValidacion["contraseñaNuevaConfirmacion"] = "Las contraseñas no coinciden"
        }

        if (erroresValidacion.isNotEmpty()) {
            _errores.value = erroresValidacion
            return
        }

        // Realizar el cambio de contraseña
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = changePasswordUseCase(claveActual, claveNueva, claveNuevaConfirmacion)
                
                if (response.status == 200) {
                    _mensaje.value = "Contraseña cambiada exitosamente"
                    _salir.value = true
                } else {
                    // Manejar errores del servidor
                    if (response.errors != null && response.errors.isNotEmpty()) {
                        _errores.value = response.errors
                    } else {
                        _mensaje.value = response.message
                    }
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al cambiar la contraseña: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}