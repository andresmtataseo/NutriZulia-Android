package com.nutrizulia.data.remote.dto.catalog

import com.google.gson.annotations.SerializedName
import com.nutrizulia.data.local.entity.user.RolEntity

data class RolResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String
)

fun RolResponseDto.toEntity() = RolEntity(
    id = id,
    nombre = nombre
)