package com.nutrizulia.data.local.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.catalog.Municipio

@Entity(
    tableName = "municipios",
    indices = [Index(value = ["estado_id"])],
    foreignKeys = [ForeignKey(
        entity = EstadoEntity::class,
        parentColumns = ["id"],
        childColumns = ["estado_id"],
        onDelete = ForeignKey.NO_ACTION
    )]
)
data class MunicipioEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "estado_id") val estadoId: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

fun Municipio.toEntity() = MunicipioEntity(
    id = id,
    estadoId = estadoId,
    nombre = nombre
)