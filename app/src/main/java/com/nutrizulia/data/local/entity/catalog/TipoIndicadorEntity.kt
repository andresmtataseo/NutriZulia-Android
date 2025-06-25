package com.nutrizulia.data.local.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.catalog.TipoIndicador

@Entity(tableName = "tipos_indicadores")
data class TipoIndicadorEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

fun TipoIndicador.toEntity() = TipoIndicadorEntity(
    id = id,
    nombre = nombre
)