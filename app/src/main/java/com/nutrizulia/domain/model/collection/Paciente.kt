package com.nutrizulia.domain.model.collection

import com.nutrizulia.data.local.entity.collection.PacienteEntity
import java.time.LocalDate
import java.time.LocalDateTime

data class Paciente(
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
 fun PacienteEntity.toDomain() = Paciente(
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