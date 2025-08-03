package com.nutrizulia.domain.model.collection

import com.nutrizulia.data.local.entity.collection.PacienteEntity
import com.nutrizulia.data.remote.dto.collection.PacienteRequestDto
import java.time.LocalDate
import java.time.LocalDateTime

data class Paciente(
    val id: String,
    var usuarioInstitucionId: Int,
    var cedula: String,
    var nombres: String,
    var apellidos: String,
    var fechaNacimiento: LocalDate,
    var genero: String,
    var etniaId: Int,
    var nacionalidadId: Int,
    var parroquiaId: Int,
    var domicilio: String,
    var telefono: String?,
    var correo: String?,
    val updatedAt: LocalDateTime,
    var isDeleted: Boolean,
    var isSynced: Boolean
)
fun PacienteEntity.toDto() = PacienteRequestDto(
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
     updatedAt = updatedAt,
     isDeleted = isDeleted,
     isSynced = isSynced
 )