package com.nutrizulia.data.local.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.catalog.GrupoEtario

@Entity(tableName = "grupos_etarios")
data class GrupoEtarioEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "edad_mes_minima") val edadMesMinima: Int,
    @ColumnInfo(name = "edad_mes_maxima") val edadMesMaxima: Int
)

fun GrupoEtario.toEntity() = GrupoEtarioEntity(
    id = id,
    nombre = nombre,
    edadMesMinima = edadMesMinima,
    edadMesMaxima = edadMesMaxima
)