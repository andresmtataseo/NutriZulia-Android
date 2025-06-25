package com.nutrizulia.domain.model.user

import com.nutrizulia.data.local.entity.user.UsuarioEntity
import java.time.LocalDate

data class Usuario(
    val id: Int,
    val cedula: String,
    val nombres: String,
    val apellidos: String,
    val fechaNacimiento: LocalDate,
    val genero: String,
    val telefono: String,
    val correo: String,
    val clave: String,
    val isEnabled: Boolean
)

fun UsuarioEntity.toDomain() = Usuario(
    id = id,
    cedula = cedula,
    nombres = nombres,
    apellidos = apellidos,
    fechaNacimiento = fechaNacimiento,
    genero = genero,
    telefono = telefono,
    correo = correo,
    clave = clave,
    isEnabled = isEnabled
)