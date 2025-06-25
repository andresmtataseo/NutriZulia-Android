package com.nutrizulia.domain.model.user

import com.nutrizulia.data.local.entity.user.RolEntity

data class Rol(
    val id: Int,
    val nombre: String
)

fun RolEntity.toDomain() = Rol(
    id = id,
    nombre = nombre
)