package com.nutrizulia.data.remote.dto.collection

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.collection.PacienteEntity
import java.time.LocalDate
import java.time.LocalDateTime

data class PacienteRequestDto (
    @SerializedName("id") val id: String,
    @SerializedName("usuario_institucion_id") val usuarioInstitucionId: Int,
    @SerializedName("cedula") val cedula: String,
    @SerializedName("nombres") val nombres: String,
    @SerializedName("apellidos") val apellidos: String,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: LocalDate,
    @SerializedName("genero") var genero: String,
    @SerializedName("etnia_id") var etniaId: Int,
    @SerializedName("nacionalidad_id") var nacionalidadId: Int,
    @SerializedName("parroquia_id") var parroquiaId: Int,
    @SerializedName("domicilio") var domicilio: String,
    @SerializedName("telefono") var telefono: String?,
    @SerializedName("correo") var correo: String?,
    @SerializedName("updated_at") val updatedAt: LocalDateTime,
    @SerializedName("is_deleted") var isDeleted: Boolean
)

fun PacienteRequestDto.toEntity() = PacienteEntity(
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