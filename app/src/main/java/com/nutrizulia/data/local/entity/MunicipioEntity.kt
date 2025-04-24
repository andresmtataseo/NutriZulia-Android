package com.nutrizulia.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.Municipio
import javax.annotation.Nonnull

@Entity(
    tableName = "municipios",
    primaryKeys = ["cod_entidad_ine", "cod_municipio_ine"],
    foreignKeys = [
        ForeignKey(
            entity = EntidadEntity::class,
            parentColumns = ["cod_entidad_ine"],
            childColumns = ["cod_entidad_ine"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class MunicipioEntity(
    @ColumnInfo(name = "cod_entidad_ine") val codEntidad: String,
    @ColumnInfo(name = "cod_municipio_ine") val codMunicipio: String,
    @ColumnInfo(name = "municipio_ine") val municipio: String
)

fun Municipio.toEntity() = MunicipioEntity(
    codEntidad = codEntidad,
    codMunicipio = codMunicipio,
    municipio = municipio
)