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

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _validationErrors = MutableLiveData<Map<String, String>>(emptyMap())
    val validationErrors: LiveData<Map<String, String>> get() = _validationErrors

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> get() = _successMessage

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun recuperarClave(tipoCedula: String, cedula: String) {
        // Limpiar mensajes previos
        clearMessages()
        _validationErrors.value = emptyMap()

        // Validar campos
        val errors = validateInput(tipoCedula, cedula)
        if (errors.isNotEmpty()) {
            _validationErrors.value = errors
            return
        }

        // Construir cédula completa
        val cedulaCompleta = "${tipoCedula.trim()}-${cedula.trim()}".uppercase()

        viewModelScope.launch {
            _loading.value = true
            
            try {
                val response = forgetPasswordUseCase(cedulaCompleta)
                
                when (response.status) {
                    200 -> {
                        _successMessage.value = response.message.ifEmpty { 
                            "Se ha enviado un enlace de recuperación a su correo electrónico registrado." 
                        }
                    }
                    400 -> {
                        // Error de validación del servidor
                        if (response.errors?.isNotEmpty() == true) {
                            _validationErrors.value = response.errors
                        } else {
                            _errorMessage.value = response.message.ifEmpty { 
                                "Los datos ingresados no son válidos." 
                            }
                        }
                    }
                    404 -> {
                        _errorMessage.value = "No se encontró un usuario registrado con esta cédula."
                    }
                    500 -> {
                        _errorMessage.value = "Error interno del servidor. Por favor, intente más tarde."
                    }
                    else -> {
                        _errorMessage.value = response.message.ifEmpty { 
                            "Ha ocurrido un error inesperado." 
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = when {
                    e.message?.contains("timeout", ignoreCase = true) == true -> 
                        "Tiempo de espera agotado. Verifique su conexión a internet."
                    e.message?.contains("network", ignoreCase = true) == true -> 
                        "Error de conexión. Verifique su conexión a internet."
                    e.message?.contains("Respuesta vacía", ignoreCase = true) == true -> 
                        "Error de comunicación con el servidor."
                    else -> 
                        "Error de conexión. Verifique su conexión a internet e intente nuevamente."
                }
            } finally {
                _loading.value = false
            }
        }
    }

    private fun validateInput(tipoCedula: String, cedula: String): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        // Validar tipo de cédula
        if (tipoCedula.isBlank()) {
            errors["tipoCedula"] = "Debe seleccionar el tipo de cédula"
        } else if (!tipoCedula.uppercase().matches(Regex("^[VE]$"))) {
            errors["tipoCedula"] = "Tipo de cédula no válido"
        }

        // Validar número de cédula
        if (cedula.isBlank()) {
            errors["cedula"] = "El número de cédula es obligatorio"
        } else {
            val cedulaLimpia = cedula.trim()
            when {
                !cedulaLimpia.matches(Regex("^\\d+$")) -> {
                    errors["cedula"] = "La cédula solo debe contener números"
                }
                cedulaLimpia.length < 6 -> {
                    errors["cedula"] = "La cédula debe tener al menos 6 dígitos"
                }
                cedulaLimpia.length > 8 -> {
                    errors["cedula"] = "La cédula no puede tener más de 8 dígitos"
                }
            }
        }

        // Validar cédula completa si no hay errores individuales
        if (errors.isEmpty()) {
            val cedulaCompleta = "${tipoCedula.trim()}-${cedula.trim()}".uppercase()
            if (!esCedulaValida(cedulaCompleta)) {
                errors["cedula"] = "Formato de cédula no válido"
            }
        }

        return errors
    }

    fun clearErrors() {
        _validationErrors.value = emptyMap()
    }

    fun clearMessages() {
        _successMessage.value = null
        _errorMessage.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}