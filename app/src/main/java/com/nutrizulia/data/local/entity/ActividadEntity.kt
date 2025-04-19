package com.nutrizulia.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import com.nutrizulia.domain.model.Actividad

@Entity(
    tableName = "actividades",
    foreignKeys = [ForeignKey(
        entity = UsuarioEntity::class,
        parentColumns = ["id"],
        childColumns = ["usuario_id"],
        onDelete = ForeignKey.NO_ACTION
    )]
)
data class ActividadEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "usuario_id") val usuarioId: Int,
    @ColumnInfo(name = "tipo_actividad") val tipoActividad: String,
    @ColumnInfo(name = "fecha") val fecha: String
)

fun Actividad.toEntity() = ActividadEntity(
    usuarioId = usuarioId,
    tipoActividad = tipoActividad,
    fecha = fecha
)