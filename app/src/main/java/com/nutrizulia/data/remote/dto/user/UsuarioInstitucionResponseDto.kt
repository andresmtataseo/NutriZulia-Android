package com.nutrizulia.data.remote.dto.user

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.user.UsuarioInstitucionEntity
import java.time.LocalDate

data class UsuarioInstitucionResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("institucion_id") val institucionId: Int,
    @SerializedName("usuario_id") val usuarioId: Int,
    @SerializedName("rol_id") val rolId: Int,
    @SerializedName("fecha_inicio") val fechaInicio: LocalDate,
    @SerializedName("fecha_fin") val fechaFin: LocalDate?,
    @SerializedName("is_enabled") val isEnabled: Boolean
)

fun UsuarioInstitucionResponseDto.toEntity() = UsuarioInstitucionEntity(
    id = id,
    institucionId = institucionId,
    usuarioId = usuarioId,
    rolId = rolId,
    fechaInicio = fechaInicio,
    fechaFin = fechaFin,
    isEnabled = isEnabled
)
