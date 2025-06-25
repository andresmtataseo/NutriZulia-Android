package com.nutrizulia.data.local.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.catalog.Enfermedad

@Entity(
    tableName = "enfermedades",
    indices = [Index(value = ["genero"])],
)
data class EnfermedadEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "codigo_internacional") val codigoInternacional: String,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "genero") val genero: String,
)

fun Enfermedad.toEntity() = EnfermedadEntity(
    id = id,
    codigoInternacional = codigoInternacional,
    nombre = nombre,
    genero = genero
)