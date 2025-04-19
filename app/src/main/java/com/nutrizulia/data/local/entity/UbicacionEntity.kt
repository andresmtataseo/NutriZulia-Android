package com.nutrizulia.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.Ubicacion

@Entity(
    tableName = "ubicaciones",
    indices = [
        Index(value = ["cod_entidad_ine", "cod_municipio_ine", "cod_parroquia_ine"]),
    ]
)
data class UbicacionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "cod_entidad_ine") val codEntidad: String,
    @ColumnInfo(name = "entidad_ine") val entidad: String,
    @ColumnInfo(name = "cod_municipio_ine") val codMunicipio: String,
    @ColumnInfo(name = "municipio_ine") val municipio: String,
    @ColumnInfo(name = "cod_parroquia_ine") val codParroquia: String,
    @ColumnInfo(name = "parroquia_ine") val parroquia: String,
)

fun Ubicacion.toEntity() = UbicacionEntity(
    codEntidad = codEntidad,
    entidad = entidad,
    codMunicipio = codMunicipio,
    municipio = municipio,
    codParroquia = codParroquia,
    parroquia = parroquia
)