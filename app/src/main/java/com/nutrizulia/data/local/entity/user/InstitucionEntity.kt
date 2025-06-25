package com.nutrizulia.data.local.entity.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nutrizulia.data.local.entity.catalog.MunicipioSanitarioEntity
import com.nutrizulia.data.local.entity.catalog.TipoInstitucionEntity
import com.nutrizulia.domain.model.user.Institucion

@Entity(
    tableName = "instituciones",
    indices = [
        Index(value = ["municipio_sanitario_id"]),
        Index(value = ["tipo_institucion_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = MunicipioSanitarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["municipio_sanitario_id"],
            onDelete = ForeignKey.NO_ACTION),
        ForeignKey(
            entity = TipoInstitucionEntity::class,
            parentColumns = ["id"],
            childColumns = ["tipo_institucion_id"],
            onDelete = ForeignKey.NO_ACTION)
    ]
)
data class InstitucionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "municipio_sanitario_id") val municipioSaniitarioId: Int,
    @ColumnInfo(name = "tipo_institucion_id") val tipoInstitucionId: Int,
    @ColumnInfo(name = "nombre") val nombre: String
)

fun Institucion.toEntity() = InstitucionEntity(
    id = id,
    municipioSaniitarioId = municipioSaniitarioId,
    tipoInstitucionId = tipoInstitucionId,
    nombre = nombre
)