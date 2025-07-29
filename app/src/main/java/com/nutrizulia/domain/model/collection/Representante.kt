package com.nutrizulia.domain.model.collection

import com.nutrizulia.data.local.entity.collection.RepresentanteEntity
import java.time.LocalDate
import java.time.LocalDateTime

data class Representante(
    val id: String,
    var usuarioInstitucionId: Int,
    var cedula: String,
    var nombres: String,
    var apellidos: String,
    val fechaNacimiento: LocalDate,
    val genero: String,
    val etniaId: Int,
    val nacionalidadId: Int,
    val parroquiaId: Int,
    val domicilio: String,
    var telefono: String?,
    var correo: String?,
    val updatedAt: LocalDateTime,
    var isDeleted: Boolean
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
    updatedAt = updatedAt,
    isDeleted = isDeleted
)