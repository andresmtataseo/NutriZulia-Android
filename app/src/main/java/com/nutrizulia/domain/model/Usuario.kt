package com.nutrizulia.domain.model

import com.nutrizulia.data.local.entity.UsuarioEntity

data class Usuario(
    val id: Int,
    var cedula: String,
    var primerNombre: String,
    var segundoNombre: String?,
    var primerApellido: String,
    var segundoApellido: String,
    var profesion: String,
    var telefono: String,
    var correo: String,
    var clave: String,
    var isActivo: Boolean
)

fun UsuarioEntity.toDomain() = Usuario(id, cedula, primerNombre, segundoNombre, primerApellido, segundoApellido, profesion, telefono, correo, clave, isActivo)

