package com.nutrizulia.data.local.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.catalog.Especialidad

@Entity(tableName = "especialidades")
data class EspecialidadEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

fun Especialidad.toEntity() = EspecialidadEntity(
    id = id,
    nombre = nombre
)