package com.nutrizulia.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.Parroquia
import javax.annotation.Nonnull

@Entity(
    tableName = "parroquias",
    primaryKeys = ["cod_entidad_ine", "cod_municipio_ine", "cod_parroquia_ine"],
    foreignKeys = [
        ForeignKey(
            entity = MunicipioEntity::class,
            parentColumns = ["cod_entidad_ine", "cod_municipio_ine"],
            childColumns = ["cod_entidad_ine", "cod_municipio_ine"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class ParroquiaEntity(
    @ColumnInfo(name = "cod_entidad_ine") val codEntidad: String,
    @ColumnInfo(name = "cod_municipio_ine") val codMunicipio: String,
    @ColumnInfo(name = "cod_parroquia_ine") val codParroquia: String,
    @Nonnull
    @ColumnInfo(name = "parroquia_ine") val parroquia: String
)

fun Parroquia.toEntity() = ParroquiaEntity(
    codEntidad = codEntidad,
    codMunicipio = codMunicipio,
    codParroquia = codParroquia,
    parroquia = parroquia
)