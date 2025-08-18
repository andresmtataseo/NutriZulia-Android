package com.nutrizulia.domain.model.collection

import com.nutrizulia.data.local.view.PacienteRepresentadoView
import java.time.LocalDate
import java.time.LocalDateTime

data class PacienteRepresentado(
    val relacionId: String,
    val usuarioInstitucionId: Int,
    val representanteId: String,
    val pacienteId: String,
    val pacienteCedula: String,
    val pacienteNombres: String,
    val pacienteApellidos: String,
    val pacienteFechaNacimiento: LocalDate,
    val pacienteGenero: String,
    val pacienteTelefono: String?,
    val pacienteCorreo: String?,
    val parentescoId: Int,
    val parentescoNombre: String,
    val ultimaActualizacion: LocalDateTime
) {
    val nombreCompletoPaciente: String
        get() = "$pacienteNombres $pacienteApellidos"
}

fun PacienteRepresentadoView.toDomain() = PacienteRepresentado(
    relacionId = relacionId,
    usuarioInstitucionId = usuarioInstitucionId,
    representanteId = representanteId,
    pacienteId = pacienteId,
    pacienteCedula = pacienteCedula,
    pacienteNombres = pacienteNombres,
    pacienteApellidos = pacienteApellidos,
    pacienteFechaNacimiento = pacienteFechaNacimiento,
    pacienteGenero = pacienteGenero,
    pacienteTelefono = pacienteTelefono,
    pacienteCorreo = pacienteCorreo,
    parentescoId = parentescoId,
    parentescoNombre = parentescoNombre,
    ultimaActualizacion = ultimaActualizacion
)