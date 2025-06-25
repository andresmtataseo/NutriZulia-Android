package com.nutrizulia.data.local.entity.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.user.UsuarioInstitucion
import java.time.LocalDate

@Entity(
    tableName = "usuarios_instituciones",
    indices = [
        Index(value = ["institucion_id"]),
        Index(value = ["usuario_id"]),
        Index(value = ["rol_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = InstitucionEntity::class,
            parentColumns = ["id"],
            childColumns = ["institucion_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuario_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = RolEntity::class,
            parentColumns = ["id"],
            childColumns = ["rol_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class UsuarioInstitucionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "institucion_id") val institucionId: Int,
    @ColumnInfo(name = "usuario_id") val usuarioId: Int,
    @ColumnInfo(name = "rol_id") val rolId: Int,
    @ColumnInfo(name = "fecha_inicio") val fechaInicio: LocalDate,
    @ColumnInfo(name = "fecha_fin") val fechaFin: LocalDate?,
    @ColumnInfo(name = "is_enabled", defaultValue = "true") val isEnabled: Boolean
)

fun UsuarioInstitucion.toEntity() = UsuarioInstitucionEntity(
    id = id,
    institucionId = institucionId,
    usuarioId = usuarioId,
    rolId = rolId,
    fechaInicio = fechaInicio,
    fechaFin = fechaFin,
    isEnabled = isEnabled
)