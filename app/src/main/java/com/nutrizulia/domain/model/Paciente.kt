package com.nutrizulia.domain.model

import com.nutrizulia.data.local.entity.PacienteEntity

data class Paciente(
    val id: Int,
    var cedula: String,
    var primerNombre: String,
    var segundoNombre: String?,
    var primerApellido: String,
    var segundoApellido: String,
    var fechaNacimiento: String,
    var genero: String,
    var etnia: String,
    var nacionalidad: String,
    var codEntidad: String,
    var codMunicipio: String,
    var codParroquia: String,
    var idComunidad: String,
    var telefono: String?,
    var correo: String?,
    var fechaIngreso: String
)

fun PacienteEntity.toDomain() = Paciente(
    id = id,
    cedula = cedula,
    primerNombre = primerNombre,
    segundoNombre = segundoNombre,
    primerApellido = primerApellido,
    segundoApellido = segundoApellido,
    fechaNacimiento = fechaNacimiento,
    genero = genero,
    etnia = etnia,
    nacionalidad = nacionalidad,
    codEntidad = codEntidad,
    codMunicipio = codMunicipio,
    codParroquia = codParroquia,
    idComunidad = idComunidad,
    telefono = telefono,
    correo = correo,
    fechaIngreso = fechaIngreso
)
