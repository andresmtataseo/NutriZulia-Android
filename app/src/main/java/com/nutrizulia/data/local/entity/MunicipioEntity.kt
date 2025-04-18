package com.nutrizulia.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "municipios",
    indices = [
        Index(value = ["estados_id"]),
    ],
    foreignKeys = [ForeignKey(
        entity = EstadoEntity::class,
        parentColumns = ["id"],
        childColumns = ["estados_id"],
        onDelete = ForeignKey.NO_ACTION
    )]
)
data class MunicipioEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "estados_id") val estadoId: Int, // Clave foránea a la tabla Estado
    @ColumnInfo(name = "nombre") val nombre: String
)
