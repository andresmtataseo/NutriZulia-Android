package com.nutrizulia.data.local.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.catalog.Nacionalidad

@Entity(tableName = "nacionalidades")
data class NacionalidadEntity (
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

fun Nacionalidad.toEntity() = NacionalidadEntity(
    id = id,
    nombre = nombre
)