package com.nutrizulia.data.local.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.catalog.TipoInstitucion

@Entity(tableName = "tipos_instituciones")
data class TipoInstitucionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

fun TipoInstitucion.toEntity() = TipoInstitucionEntity(
    id = id,
    nombre = nombre
)