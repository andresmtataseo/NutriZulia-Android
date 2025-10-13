package com.nutrizulia.presentation.viewmodel.consulta

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.view.PacienteConCita
import com.nutrizulia.domain.usecase.collection.GetPacienteConCitaById
import com.nutrizulia.domain.usecase.collection.SaveConsultaEstadoById
import com.nutrizulia.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.nutrizulia.util.Event
import com.nutrizulia.domain.usecase.collection.GetPacienteById
import com.nutrizulia.domain.usecase.dashboard.GetCurrentUserDataUseCase
import com.nutrizulia.domain.usecase.dashboard.CurrentUserDataResult
import java.net.URLEncoder
import java.time.format.DateTimeFormatter
import java.util.Locale

@HiltViewModel
class AccionesCitaViewModel @Inject constructor(
    private val getPacienteConCitaById: GetPacienteConCitaById,
    private val sessionManager: SessionManager,
    private val saveConsultaEstadoById: SaveConsultaEstadoById,
    private val getPacienteById: GetPacienteById,
    private val getCurrentUserDataUseCase: GetCurrentUserDataUseCase
) : ViewModel() {

    private val _pacienteConCita = MutableLiveData<PacienteConCita>()
    val pacienteConCita: LiveData<PacienteConCita> get() = _pacienteConCita
    private var _idUsuarioInstitucion = MutableLiveData<Int>()
    val idUsuarioInstitucion: LiveData<Int> get() = _idUsuarioInstitucion

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _salir = MutableLiveData<Boolean>()
    val salir: LiveData<Boolean> get() = _salir

    // Evento para abrir WhatsApp con el mensaje predefinido
    private val _openWhatsApp = MutableLiveData<Event<String>>()
    val openWhatsApp: LiveData<Event<String>> get() = _openWhatsApp

    // Estado para controlar si se puede enviar WhatsApp (si el paciente tiene teléfono válido)
    private val _canSendWhatsApp = MutableLiveData<Boolean>()
    val canSendWhatsApp: LiveData<Boolean> get() = _canSendWhatsApp

    fun onCreate(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val pacienteJob = launch { obtenerPacienteConCita(id) }
                pacienteJob.join()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun obtenerPacienteConCita(consultaId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            sessionManager.currentInstitutionIdFlow.firstOrNull()?.let { institutionId ->
                _idUsuarioInstitucion.value = institutionId
            } ?: run {
                _mensaje.value = "No se ha seleccionado una institución."
                _isLoading.value = false
                _salir.value = true
                return@launch
            }

            val result = getPacienteConCitaById(idUsuarioInstitucion.value ?: 0, consultaId)
            if (result != null) {
                _pacienteConCita.value = result

                // Actualizar disponibilidad de WhatsApp según el teléfono del paciente
                val paciente = getPacienteById(_idUsuarioInstitucion.value ?: 0, result.pacienteId)
                val telefonoWhatsApp = formatPhoneForWhatsApp(paciente?.telefono)
                _canSendWhatsApp.value = telefonoWhatsApp != null
            } else {
                _mensaje.value = "No se encontraron datos."
                _isLoading.value = false
                _salir.value = true
                return@launch
            }
            _isLoading.value = false
        }
    }

    fun cancelarCita(idConsulta: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                saveConsultaEstadoById(idConsulta, Estado.CANCELADA)
                _mensaje.value = "Cita cancelada con éxito."
                _salir.value = true
            } catch (e: Exception) {
                _mensaje.value = "Error al cancelar la cita."
            } finally {
                _isLoading.value = false
            }

        }

    }

    // Botón para enviar recordatorio por WhatsApp
    fun enviarRecordatorioWhatsApp() {
        viewModelScope.launch {
            val usuarioInstitucionId = idUsuarioInstitucion.value
            val cita = pacienteConCita.value

            if (usuarioInstitucionId == null) {
                _mensaje.value = "No se ha seleccionado una institución."
                return@launch
            }
            if (cita == null) {
                _mensaje.value = "No hay datos de la cita para enviar el recordatorio."
                return@launch
            }

            // Obtener teléfono del paciente
            val paciente = getPacienteById(usuarioInstitucionId, cita.pacienteId)
            val telefonoRaw = paciente?.telefono
            val telefonoWhatsApp = formatPhoneForWhatsApp(telefonoRaw)

            if (telefonoWhatsApp == null) {
                _mensaje.value = "El paciente no tiene un teléfono válido para WhatsApp."
                return@launch
            }

            // Construir mensaje
            val fechaHora = cita.fechaHoraProgramadaConsulta ?: cita.fechaHoraRealConsulta
            val fechaHoraStr = if (fechaHora != null) {
                val fecha = fechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es", "ES")))
                val hora = fechaHora.format(DateTimeFormatter.ofPattern("hh:mm a", Locale("es", "ES")))
                "$fecha a las $hora"
            } else {
                "No disponible"
            }

            val institutionName = when (val userDataResult = getCurrentUserDataUseCase()) {
                is CurrentUserDataResult.Success -> userDataResult.userData.nombreInstitucion
                else -> "su institución"
            }

            val tipo = cita.tipoConsulta.name.replace('_', ' ').lowercase(Locale("es", "ES")).replaceFirstChar { it.titlecase(Locale("es", "ES")) }
            val especialidad = cita.nombreEspecialidadRemitente ?: "General"
            val estado = cita.estadoConsulta.name.lowercase(Locale("es", "ES")).replaceFirstChar { it.titlecase(Locale("es", "ES")) }

            val mensaje = "Hola ${cita.nombreCompleto} 👋\n\n" +
                "Te recordamos tu cita:\n" +
                "• Fecha y hora: $fechaHoraStr\n" +
                "• Institución: $institutionName\n" +
                "• Tipo de consulta: $tipo\n" +
                "• Cédula: ${cita.cedulaPaciente}\n\n" +
                "Por favor, confirma tu asistencia respondiendo este mensaje. ¡Gracias!"
            val mensajeEncoded = URLEncoder.encode(mensaje, "UTF-8")
            val uri = "https://wa.me/$telefonoWhatsApp?text=$mensajeEncoded"

            // Emitir evento para abrir WhatsApp
            _openWhatsApp.value = Event(uri)
        }
    }

    private fun formatPhoneForWhatsApp(phoneRaw: String?): String? {
        val digits = phoneRaw?.filter { it.isDigit() } ?: return null
        if (digits.isEmpty()) return null
        if (digits.startsWith("58")) {
            val rest = digits.removePrefix("58").trimStart('0')
            return if (rest.length >= 10) {
                "58" + rest.take(10)
            } else null
        }

        if (digits.startsWith("0")) {
            val rest = digits.drop(1)
            return if (rest.length >= 10) {
                "58" + rest.take(10)
            } else null
        }

        if (digits.length == 10) {
            return "58" + digits
        }

        return null
    }
}