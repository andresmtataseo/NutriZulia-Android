package com.nutrizulia.domain.model.catalog

import com.nutrizulia.data.local.entity.catalog.VersionEntity

data class Version(
    val nombreTabla: String,
    val version: Int,
    val isUpdated: Boolean = false,
)

fun VersionEntity.toDomain() = Version(
    nombreTabla = nombreTabla,
    version = version,
    isUpdated = isUpdated
)