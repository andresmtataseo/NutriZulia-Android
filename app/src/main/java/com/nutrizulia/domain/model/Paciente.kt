package com.nutrizulia.domain.model

import com.nutrizulia.data.local.entity.PacienteEntity

data class Paciente(
    val id: Int,
    var cedula: String,
    var primerNombre: String,
    var segundoNombre: String,
    var primerApellido: String,
    var segundoApellido: String,
    var fechaNacimiento: String,
    var genero: String,
    var etnia: String,
    var nacionalidad: String,
    var grupoSanguineo: String,
    var parroquia: Int,
    var telefono: String,
    var correo: String,
    var fechaIngreso: String
)

fun PacienteEntity.toDomain() = Paciente(id, cedula, primerNombre, segundoNombre, primerApellido, segundoApellido, fechaNacimiento, genero, etnia, nacionalidad, grupoSanguineo, parroquiasId, telefono, correo, fechaIngreso)
