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
import com.nutrizulia.domain.usecase.collection.GetPacienteRepresentanteByPacienteId
import com.nutrizulia.domain.usecase.collection.GetRepresentanteById
import com.nutrizulia.domain.usecase.dashboard.GetCurrentUserDataUseCase
import com.nutrizulia.domain.usecase.dashboard.CurrentUserDataResult

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

@HiltViewModel
class AccionesCitaViewModel @Inject constructor(
    private val getPacienteConCitaById: GetPacienteConCitaById,
    private val sessionManager: SessionManager,
    private val saveConsultaEstadoById: SaveConsultaEstadoById,
    private val getPacienteById: GetPacienteById,
    private val getCurrentUserDataUseCase: GetCurrentUserDataUseCase,
    private val getPacienteRepresentanteByPacienteId: GetPacienteRepresentanteByPacienteId,
    private val getRepresentanteById: GetRepresentanteById
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

                // Actualizar disponibilidad de WhatsApp considerando fallback al representante
                val paciente = getPacienteById(_idUsuarioInstitucion.value ?: 0, result.pacienteId)
                val telefonoPaciente = formatPhoneForWhatsApp(paciente?.telefono)
                var puedeEnviar = telefonoPaciente != null

                if (!puedeEnviar && paciente != null) {
                    val relacion = getPacienteRepresentanteByPacienteId(paciente.id)
                    val telefonoRepresentante = relacion?.let {
                        val rep = getRepresentanteById(_idUsuarioInstitucion.value ?: 0, it.representanteId)
                        formatPhoneForWhatsApp(rep?.telefono)
                    }
                    puedeEnviar = telefonoRepresentante != null
                }
                _canSendWhatsApp.value = puedeEnviar
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

            // Obtener teléfono del paciente y aplicar fallback al representante si es pediátrico y no tiene teléfono
            val paciente = getPacienteById(usuarioInstitucionId, cita.pacienteId)
            val telefonoRawPaciente = paciente?.telefono
            var telefonoWhatsApp = formatPhoneForWhatsApp(telefonoRawPaciente)
            var enviarARepresentante = false
            var nombreRepresentanteCorto: String? = null


            if (telefonoWhatsApp == null && paciente != null) {
                val relacion = getPacienteRepresentanteByPacienteId(paciente.id)
                if (relacion != null) {
                    val representante = getRepresentanteById(usuarioInstitucionId, relacion.representanteId)
                    val telefonoRep = formatPhoneForWhatsApp(representante?.telefono)
                    if (telefonoRep != null && representante != null) {
                        telefonoWhatsApp = telefonoRep
                        enviarARepresentante = true
                        nombreRepresentanteCorto = formatShortName(representante.nombres, representante.apellidos)
                    }
                }
            }

            if (telefonoWhatsApp == null) {
                _mensaje.value = "El paciente no tiene un teléfono válido para WhatsApp; si es pediátrico, no se encontró teléfono válido del representante legal."
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

            val (nutritionistName, institutionName) = when (val userDataResult = getCurrentUserDataUseCase()) {
                is CurrentUserDataResult.Success -> userDataResult.userData.nombreUsuario to userDataResult.userData.nombreInstitucion
                else -> "su nutricionista" to "su institución"
            }

            val tipo = cita.tipoConsulta.name.replace('_', ' ').lowercase(Locale("es", "ES")).replaceFirstChar { it.titlecase(Locale("es", "ES")) }

            val mensaje = if (!enviarARepresentante) {
                "Hola ${cita.nombreCompleto} 👋\n\n" +
                "Te recordamos tu cita:\n" +
                "• Fecha y hora: $fechaHoraStr\n" +
                "• Institución: $institutionName\n" +
                "• Nutricionista: $nutritionistName\n" +
                "• Tipo de consulta: $tipo\n" +
                "• Cédula: ${cita.cedulaPaciente}\n\n" +
                "Por favor, confirma tu asistencia respondiendo este mensaje. ¡Gracias!"
            } else {
                val nombreRep = nombreRepresentanteCorto ?: "Estimado/a representante"
                "Hola $nombreRep 👋\n\n" +
                "Le recordamos la cita de ${cita.nombreCompleto}:\n" +
                "• Fecha y hora: $fechaHoraStr\n" +
                "• Institución: $institutionName\n" +
                "• Nutricionista: $nutritionistName\n" +
                "• Tipo de consulta: $tipo\n" +
                "• Cédula: ${cita.cedulaPaciente}\n\n" +
                "Por favor, confirma tu asistencia respondiendo este mensaje. ¡Gracias!"
            }

            val uri = android.net.Uri.parse("https://wa.me/")
                .buildUpon()
                .appendPath(telefonoWhatsApp)
                .appendQueryParameter("text", mensaje)
                .build()
                .toString()

            // Emitir evento para abrir WhatsApp
            _openWhatsApp.value = Event(uri)
        }
    }

    private fun formatPhoneForWhatsApp(phoneRaw: String?): String? {
        val digits = phoneRaw?.filter { it.isDigit() } ?: return null
        if (digits.isEmpty()) return null

        return when {
            digits.startsWith("58") -> digits
            digits.startsWith("0") -> "58" + digits.drop(1)
            digits.length == 10 -> "58" + digits
            else -> null
        }
    }

    private fun formatShortName(nombres: String, apellidos: String): String {
        val firstName = nombres.trim().split("\\s+".toRegex()).firstOrNull() ?: nombres.trim()
        val firstSurname = apellidos.trim().split("\\s+".toRegex()).firstOrNull() ?: apellidos.trim()
        return "$firstName $firstSurname".trim()
    }

}