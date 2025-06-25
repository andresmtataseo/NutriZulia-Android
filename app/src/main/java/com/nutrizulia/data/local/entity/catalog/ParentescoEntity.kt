package com.nutrizulia.data.local.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.catalog.Parentesco

@Entity(tableName = "parentescos")
data class ParentescoEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

fun Parentesco.toEntity() = ParentescoEntity(
    id = id,
    nombre = nombre
)