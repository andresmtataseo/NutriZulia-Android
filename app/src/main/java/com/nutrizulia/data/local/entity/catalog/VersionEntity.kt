package com.nutrizulia.data.local.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.catalog.Version

@Entity(tableName = "version")
data class VersionEntity(
    @PrimaryKey
    @ColumnInfo(name = "nombre_tabla") val nombreTabla: String,
    @ColumnInfo(name = "version") val version: Int,
    @ColumnInfo(name = "is_updated") val isUpdated: Boolean = false
)

fun Version.toEntity() = VersionEntity(
    nombreTabla = nombreTabla,
    version = version,
    isUpdated = isUpdated,
)