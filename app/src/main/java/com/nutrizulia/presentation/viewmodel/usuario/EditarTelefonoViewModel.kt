package com.nutrizulia.presentation.viewmodel.usuario

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.user.Usuario
import com.nutrizulia.domain.usecase.user.CheckPhoneNumber
import com.nutrizulia.domain.usecase.user.GetUserDetails
import com.nutrizulia.domain.usecase.user.SaveTelefono
import com.nutrizulia.util.CheckData.esNumeroTelefonoValido
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditarTelefonoViewModel @Inject constructor(
    private val getUsuarioDetails: GetUserDetails,
    private val checkPhoneNumber: CheckPhoneNumber,
    private val saveTelefono: SaveTelefono
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

    fun onSaveTelefonoClicked(telefono: String?) {
        val erroresValidacion = validarTelefono(telefono)
        _errores.value = erroresValidacion
        
        if (erroresValidacion.isNotEmpty()) {
            return
        }
        
        if (telefono != null) {
            checkPhoneAndSave(telefono)
        }
    }

    private fun validarTelefono(telefono: String?): Map<String, String> {
        val errores = mutableMapOf<String, String>()
        
        if (telefono.isNullOrBlank()) {
            errores["telefono"] = "El teléfono es requerido"
        } else if (!esNumeroTelefonoValido(telefono)) {
            errores["telefono"] = "El número de teléfono no es válido"
        }
        
        return errores
    }

    private fun checkPhoneAndSave(telefono: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // Verificar disponibilidad del teléfono
                val isAvailable = checkPhoneNumber(telefono)
                
                if (!isAvailable) {
                    _errores.value = mapOf("telefono" to "El número de teléfono ya está en uso")
                    _isLoading.value = false
                    return@launch
                }
                
                // Si está disponible, guardar el teléfono
                val usuarioActual = _usuario.value
                if (usuarioActual != null) {
                    val result = saveTelefono(usuarioActual.id, telefono)
                    
                    result.fold(
                        onSuccess = { response ->
                            _mensaje.value = response.message
                            _salir.value = true
                        },
                        onFailure = { exception ->
                            _mensaje.value = "Error al guardar el teléfono: ${exception.message}"
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