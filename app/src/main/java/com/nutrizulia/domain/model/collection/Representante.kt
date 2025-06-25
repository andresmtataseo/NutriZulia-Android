package com.nutrizulia.domain.model.collection

import com.nutrizulia.data.local.entity.collection.RepresentanteEntity
import java.time.LocalDate
import java.time.LocalDateTime

data class Representante(
    val id: String,
    val usuarioInstitucionId: Int,
    val cedula: String,
    val nombres: String,
    val apellidos: String,
    val fechaNacimiento: LocalDate,
    val genero: String,
    val etniaId: Int,
    val nacionalidadId: Int,
    val parroquiaId: Int,
    val domicilio: String,
    val telefono: String?,
    val correo: String?,
    val updatedAt: LocalDateTime
)

fun RepresentanteEntity.toDomain() = Representante(
    id = id,
    usuarioInstitucionId = usuarioInstitucionId,
    cedula = cedula,
    nombres = nombres,
    apellidos = apellidos,
    fechaNacimiento = fechaNacimiento,
    genero = genero,
    etniaId = etniaId,
    nacionalidadId = nacionalidadId,
    parroquiaId = parroquiaId,
    domicilio = domicilio,
    telefono = telefono,
    correo = correo,
    updatedAt = updatedAt
)