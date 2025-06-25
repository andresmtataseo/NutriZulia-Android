package com.nutrizulia.data.local.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.catalog.RiesgoBiologico

@Entity(
    tableName = "riesgos_biologicos",
    indices = [Index(value = ["genero"])],
)
data class RiesgoBiologicoEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "genero") val genero: String,
    @ColumnInfo(name = "edad_mes_minima") val edadMesMinima: Int?,
    @ColumnInfo(name = "edad_mes_maxima") val edadMesMaxima: Int?
)

fun RiesgoBiologico.toEntity() = RiesgoBiologicoEntity(
    id = id,
    nombre = nombre,
    genero = genero,
    edadMesMinima = edadMesMinima,
    edadMesMaxima = edadMesMaxima
)