package com.nutrizulia.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.domain.usecase.dashboard.GetDashboardStatsUseCase
import com.nutrizulia.domain.usecase.dashboard.GetProximasConsultasUseCase
import com.nutrizulia.domain.usecase.dashboard.GetCurrentUserDataUseCase
import com.nutrizulia.domain.usecase.dashboard.GetCitasDelDiaUseCase
import com.nutrizulia.domain.usecase.dashboard.GetTotalPendingRecordsUseCase
import com.nutrizulia.domain.usecase.dashboard.CurrentUserDataResult
import com.nutrizulia.domain.model.dashboard.ResumenMensual
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProximaConsulta(
    val nombrePaciente: String,
    val fechaHora: String,
    val consultaId: String
)

data class DatosUsuario(
    val nombreUsuario: String,
    val nombreInstitucion: String
)

data class CitasDelDia(
    val programadas: Int,
    val completadas: Int,
    val sinPreviaCita: Int,
    val canceladas: Int
)

@HiltViewModel
class InicioViewModel @Inject constructor(
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase,
    private val getProximasConsultasUseCase: GetProximasConsultasUseCase,
    private val getCurrentUserDataUseCase: GetCurrentUserDataUseCase,
    private val getCitasDelDiaUseCase: GetCitasDelDiaUseCase,
    private val getTotalPendingRecordsUseCase: GetTotalPendingRecordsUseCase
) : ViewModel() {

    private val _proximaConsulta = MutableLiveData<ProximaConsulta?>()
    val proximaConsulta: LiveData<ProximaConsulta?> = _proximaConsulta

    private val _resumenMensual = MutableLiveData<ResumenMensual?>()
    val resumenMensual: LiveData<ResumenMensual?> = _resumenMensual

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _datosUsuario = MutableLiveData<DatosUsuario>()
    val datosUsuario: LiveData<DatosUsuario> = _datosUsuario

    private val _notificacionesPendientes = MutableLiveData<Int>()
    val notificacionesPendientes: LiveData<Int> = _notificacionesPendientes

    private val _citasDelDia = MutableLiveData<CitasDelDia>()
    val citasDelDia: LiveData<CitasDelDia> = _citasDelDia
    
    private val _citasDelDiaDetalle = MutableLiveData<List<com.nutrizulia.domain.usecase.dashboard.CitaDelDia>>()
    val citasDelDiaDetalle: LiveData<List<com.nutrizulia.domain.usecase.dashboard.CitaDelDia>> = _citasDelDiaDetalle
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _archivosPendientes = MutableLiveData<Int>()
    val archivosPendientes: LiveData<Int> = _archivosPendientes

    fun loadDashboardData() {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null
            
            try {
                // Obtener datos del usuario actual y su institución
                val userDataResult = getCurrentUserDataUseCase()
                when (userDataResult) {
                    is CurrentUserDataResult.Success -> {
                        val userData = userDataResult.userData
                        _datosUsuario.value = DatosUsuario(
                            nombreUsuario = userData.nombreUsuario,
                            nombreInstitucion = userData.nombreInstitucion
                        )
                        
                        // Cargar datos del dashboard usando el ID de la institución
                        loadDashboardDataForInstitution(userData.usuarioInstitucionId)
                    }
                    is CurrentUserDataResult.NotAuthenticated -> {
                        _errorMessage.value = "No hay sesión activa"
                    }
                    is CurrentUserDataResult.NoInstitutionSelected -> {
                        _errorMessage.value = "No se ha seleccionado una institución"
                    }
                    is CurrentUserDataResult.Error -> {
                        _errorMessage.value = userDataResult.message
                    }
                    else -> {
                        _errorMessage.value = "Error al cargar datos del usuario"
                    }
                }
                
                // Cargar archivos pendientes para la institución actual
                loadArchivosPendientes()
                
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar datos: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
    
    private suspend fun loadDashboardDataForInstitution(usuarioInstitucionId: Int) {
        try {
            // Cargar estadísticas del dashboard
            val resumenMensual = getDashboardStatsUseCase(usuarioInstitucionId)
            _resumenMensual.value = resumenMensual
            
            // Cargar próximas consultas
            val proximasConsultas = getProximasConsultasUseCase(usuarioInstitucionId)
            _proximaConsulta.value = proximasConsultas.firstOrNull()?.let {
                ProximaConsulta(
                    nombrePaciente = it.nombrePaciente,
                    fechaHora = it.fechaHora,
                    consultaId = it.consultaId
                )
            }
            
            // Cargar citas del día
            val citasHoy = getCitasDelDiaUseCase(usuarioInstitucionId)
            _citasDelDiaDetalle.value = citasHoy
            _citasDelDia.value = CitasDelDia(
                programadas = citasHoy.count { it.estado == "PENDIENTE" || it.estado == "REPROGRAMADA" },
                completadas = citasHoy.count { it.estado == "REALIZADA" || it.estado == "COMPLETADA" },
                sinPreviaCita = citasHoy.count { it.estado == "SIN_PREVIA_CITA" },
                canceladas = citasHoy.count { it.estado == "CANCELADA" }
            )
            
            // Por ahora, notificaciones pendientes será 0 (se puede implementar después)
            _notificacionesPendientes.value = 0
            
        } catch (e: Exception) {
            _errorMessage.value = "Error al cargar datos de la institución: ${e.message}"
        }
    }

    private suspend fun loadArchivosPendientes() {
        try {
            val pendientes = getTotalPendingRecordsUseCase()
            _archivosPendientes.value = pendientes
        } catch (e: Exception) {
            _archivosPendientes.value = 0
        }
    }
    
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}