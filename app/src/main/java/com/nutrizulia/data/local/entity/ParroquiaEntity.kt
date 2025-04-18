package com.nutrizulia.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "parroquias",
    indices = [
        Index(value = ["municipios_id"]),
    ],
    foreignKeys = [ForeignKey(
        entity = MunicipioEntity::class,
        parentColumns = ["id"],
        childColumns = ["municipios_id"],
        onDelete = ForeignKey.NO_ACTION
    )]
)
data class ParroquiaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "municipios_id") val municipioId: Int, // Clave foránea a la tabla Municipio
    @ColumnInfo(name = "nombre") val nombre: String
)
