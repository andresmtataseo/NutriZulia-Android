package com.nutrizulia.data.local.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.catalog.Parroquia

@Entity(
    tableName = "parroquias",
    indices = [Index(value = ["municipio_id"])],
    foreignKeys = [ForeignKey(
        entity = MunicipioEntity::class,
        parentColumns = ["id"],
        childColumns = ["municipio_id"],
        onDelete = ForeignKey.NO_ACTION
    )]
)
data class ParroquiaEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "municipio_id") val municipioId: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

fun Parroquia.toEntity() = ParroquiaEntity(
    id = id,
    municipioId = municipioId,
    nombre = nombre
)