package com.nutrizulia.data.remote.dto.user

import com.google.gson.annotations.SerializedName
import com.nutrizulia.domain.model.user.Usuario
import java.time.LocalDate

data class UsuarioResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("cedula") val cedula: String,
    @SerializedName("nombres") val nombres: String,
    @SerializedName("apellidos") val apellidos: String,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: LocalDate,
    @SerializedName("genero") val genero: String,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("correo") val correo: String,
    @SerializedName("clave") val clave: String,
    @SerializedName("is_enabled") val isEnabled: Boolean
)

fun UsuarioResponseDto.toDomain() = Usuario (
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