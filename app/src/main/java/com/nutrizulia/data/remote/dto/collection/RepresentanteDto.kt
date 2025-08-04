package com.nutrizulia.data.remote.dto.collection

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.collection.RepresentanteEntity
import java.time.LocalDate
import java.time.LocalDateTime

data class RepresentanteDto(
    @SerializedName("id") val id: String,
    @SerializedName("usuario_institucion_id") val usuarioInstitucionId: Int,
    @SerializedName("cedula") val cedula: String,
    @SerializedName("nombres") val nombres: String,
    @SerializedName("apellidos") val apellidos: String,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: LocalDate,
    @SerializedName("genero") val genero: String,
    @SerializedName("etnia_id") val etniaId: Int,
    @SerializedName("nacionalidad_id") val nacionalidadId: Int,
    @SerializedName("parroquia_id") val parroquiaId: Int,
    @SerializedName("domicilio") val domicilio: String,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("correo") val correo: String?,
    @SerializedName("updated_at") val updatedAt: LocalDateTime,
    @SerializedName("is_deleted") val isDeleted: Boolean,
)

fun RepresentanteDto.toEntity() = RepresentanteEntity(
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
    isSynced = false
)