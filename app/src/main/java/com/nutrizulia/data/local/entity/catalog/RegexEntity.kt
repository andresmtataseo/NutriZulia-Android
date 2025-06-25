package com.nutrizulia.data.local.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.catalog.Regex

@Entity(tableName = "regex")
data class RegexEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "expresion") val expresion: String
)

fun Regex.toEntity() = RegexEntity(
    id = id,
    nombre = nombre,
    expresion = expresion
)