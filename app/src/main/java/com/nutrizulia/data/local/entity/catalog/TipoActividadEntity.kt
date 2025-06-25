package com.nutrizulia.data.local.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.catalog.TipoActividad

@Entity(tableName = "tipos_actividades")
data class TipoActividadEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

fun TipoActividad.toEntity() = TipoActividadEntity(
    id = id,
    nombre = nombre
)