package com.nutrizulia.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.Entidad

@Entity(tableName = "entidades")
data class EntidadEntity(
    @PrimaryKey
    @ColumnInfo(name = "cod_entidad_ine") val codEntidad: String,
    @ColumnInfo(name = "entidad_ine") val entidad: String
)

fun Entidad.toEntity() = EntidadEntity(
    codEntidad = codEntidad,
    entidad = entidad
)