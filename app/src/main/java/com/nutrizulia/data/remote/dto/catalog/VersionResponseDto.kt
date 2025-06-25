package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.catalog.VersionEntity

data class VersionResponseDto(
    @SerializedName("nombre_tabla") val nombreTabla: String,
    @SerializedName("version") val version: Int,
)

fun VersionResponseDto.toDomain() = VersionEntity(
    nombreTabla = nombreTabla,
    version = version
)