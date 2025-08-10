package com.nutrizulia.presentation.viewmodel.usuario

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.model.user.Usuario
import com.nutrizulia.domain.usecase.user.GetPerfilesInstitucionales
import com.nutrizulia.domain.usecase.user.GetPerfilesResult
import com.nutrizulia.domain.usecase.user.GetUserDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CuentaViewModel @Inject constructor(
    private val getUsuarioDetails: GetUserDetails,
    private val getPerfilesInstitucionales: GetPerfilesInstitucionales
) : ViewModel() {

    private val _usuario = MutableLiveData<Usuario>()
    val usuario: LiveData<Usuario> = _usuario
    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> get() = _salir

    fun onCreate() {
        viewModelScope.launch {
            _isLoading.value = true
            val perfil = getPerfilesInstitucionales()
            if (perfil is GetPerfilesResult.Success) {
                val id = perfil.perfiles.first().usuarioId
                val usuario = getUsuarioDetails(id)
                if (usuario == null) {
                    _mensaje.value = "No se pudo obtener el usuario."
                    _salir.value = true
                    return@launch
                }
                _usuario.value = usuario
            } else {
                _mensaje.value = "No se pudo obtener las asignaciones de instituciones."
                _salir.value = true
                return@launch
            }
            _isLoading.value = false
        }
    }
}