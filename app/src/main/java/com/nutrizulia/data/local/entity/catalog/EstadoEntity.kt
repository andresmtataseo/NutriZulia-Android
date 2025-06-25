package com.nutrizulia.data.local.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.catalog.Estado

@Entity(tableName = "estados")
data class EstadoEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

fun Estado.toEntity() = EstadoEntity(
    id = id,
    nombre = nombre
)