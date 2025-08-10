package com.nutrizulia.presentation.viewmodel.usuario

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.user.Usuario
import com.nutrizulia.domain.usecase.user.GetUserDetails
import com.nutrizulia.domain.usecase.user.SaveEmail
import com.nutrizulia.domain.usecase.user.CheckEmail
import com.nutrizulia.util.CheckData.esCorreoValido
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditarCorreoViewModel @Inject constructor(
    private val getUsuarioDetails: GetUserDetails,
    private val checkEmail: CheckEmail,
    private val saveEmail: SaveEmail
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

    fun onCreate(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val usuario = getUsuarioDetails(id)
            if (usuario == null) {
                _mensaje.value = "No se pudo obtener el usuario."
                _salir.value = true
                return@launch
            }
            _usuario.value = usuario
            _isLoading.value = false
        }
    }

    fun onSaveCorreoClicked(correo: String?) {
        val erroresValidacion = validarCorreo(correo)
        _errores.value = erroresValidacion
        
        if (erroresValidacion.isNotEmpty()) {
            return
        }
        
        if (correo != null) {
            checkEmailAndSave(correo)
        }
    }

    private fun validarCorreo(correo: String?): Map<String, String> {
        val errores = mutableMapOf<String, String>()
        
        if (correo.isNullOrBlank()) {
            errores["correo"] = "El correo es requerido"
        } else if (!esCorreoValido(correo)) {
            errores["correo"] = "El formato del correo no es válido"
        }
        
        return errores
    }

    private fun checkEmailAndSave(correo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // Verificar disponibilidad del correo
                val isAvailable = checkEmail(correo)
                
                if (!isAvailable) {
                    _errores.value = mapOf("correo" to "El correo ya está en uso")
                    _isLoading.value = false
                    return@launch
                }
                
                // Si está disponible, guardar el correo
                val usuarioActual = _usuario.value
                if (usuarioActual != null) {
                    val result = saveEmail(usuarioActual.id, correo)
                    
                    result.fold(
                        onSuccess = { response ->
                            _mensaje.value = response.message
                            _salir.value = true
                        },
                        onFailure = { exception ->
                            _mensaje.value = "Error al guardar el correo: ${exception.message}"
                        }
                    )
                } else {
                    _mensaje.value = "Error: Usuario no encontrado"
                }
                
            } catch (e: Exception) {
                _mensaje.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun saveEmailData(correo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                val usuarioActual = _usuario.value
                if (usuarioActual != null) {
                    val result = saveEmail(usuarioActual.id, correo)
                    
                    result.fold(
                        onSuccess = { response ->
                            _mensaje.value = response.message
                            _salir.value = true
                        },
                        onFailure = { exception ->
                            val errorMessage = exception.message ?: "Error desconocido"
                            if (errorMessage.contains("duplicado") || errorMessage.contains("duplicate")) {
                                _errores.value = mapOf("correo" to "El correo ya está en uso")
                            } else {
                                _mensaje.value = "Error al guardar el correo: $errorMessage"
                            }
                        }
                    )
                } else {
                    _mensaje.value = "Error: Usuario no encontrado"
                }
                
            } catch (e: Exception) {
                _mensaje.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}