package com.nutrizulia.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.nutrizulia.domain.model.Comunidad
import javax.annotation.Nonnull

@Entity(
    tableName = "comunidades",
    primaryKeys = ["cod_entidad_ine", "cod_municipio_ine", "cod_parroquia_ine", "id_comunidad_ine"],
    foreignKeys = [
        ForeignKey(
            entity = ParroquiaEntity::class,
            parentColumns = ["cod_entidad_ine","cod_municipio_ine","cod_parroquia_ine"],
            childColumns = ["cod_entidad_ine","cod_municipio_ine","cod_parroquia_ine"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class ComunidadEntity(
    @ColumnInfo(name = "cod_entidad_ine") val codEntidad: String,
    @ColumnInfo(name = "cod_municipio_ine") val codMunicipio: String,
    @ColumnInfo(name = "cod_parroquia_ine") val codParroquia: String,
    @ColumnInfo(name = "id_comunidad_ine") val idComunidad: String,
    @ColumnInfo(name = "nombre_comunidad") val nombreComunidad: String
)

fun Comunidad.toEntity() = ComunidadEntity(
    codEntidad = codEntidad,
    codMunicipio = codMunicipio,
    codParroquia = codParroquia,
    idComunidad = idComunidad,
    nombreComunidad = nombreComunidad
)