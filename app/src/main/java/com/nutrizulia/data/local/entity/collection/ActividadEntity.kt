package com.nutrizulia.data.local.entity.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.data.local.entity.catalog.TipoActividadEntity
import com.nutrizulia.data.local.entity.user.UsuarioInstitucionEntity
import com.nutrizulia.domain.model.collection.Actividad
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "actividades",
    indices = [
        Index(value = ["usuario_institucion_id"]),
        Index(value = ["tipo_actividad_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UsuarioInstitucionEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuario_institucion_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = TipoActividadEntity::class,
            parentColumns = ["id"],
            childColumns = ["tipo_actividad_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class ActividadEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "usuario_institucion_id") val usuarioInstitucionId: Int,
    @ColumnInfo(name = "tipo_actividad_id") val tipoActividadId: Int,
    @ColumnInfo(name = "fecha") val fecha: LocalDate,
    @ColumnInfo(name = "direccion") val direccion: String?,
    @ColumnInfo(name = "descripcion_general") val descripcionGeneral: String?,
    @ColumnInfo(name = "cantidad_participantes") val cantidadParticipantes: Int?,
    @ColumnInfo(name = "cantidad_sesiones") val cantidadSesiones: Int?,
    @ColumnInfo(name = "duracion_minutos") val duracionMinutos: Int?,
    @ColumnInfo(name = "tema_principal") val temaPrincipal: String?,
    @ColumnInfo(name = "programas_implementados") val programasImplementados: String?,
    @ColumnInfo(name = "url_evidencia") val urlEvidencia: String?,
    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP") val updatedAt: LocalDateTime,
    @ColumnInfo(name = "is_deleted", defaultValue = "0") val isDeleted: Boolean,
    @ColumnInfo(name = "is_synced", defaultValue = "0") val isSynced: Boolean
)

fun Actividad.toEntity() = ActividadEntity(
    id = id,
    usuarioInstitucionId = usuarioInstitucionId,
    tipoActividadId = tipoActividadId,
    fecha = fecha,
    direccion = direccion,
    descripcionGeneral = descripcionGeneral,
    cantidadParticipantes = cantidadParticipantes,
    cantidadSesiones = cantidadSesiones,
    duracionMinutos = duracionMinutos,
    temaPrincipal = temaPrincipal,
    programasImplementados = programasImplementados,
    urlEvidencia = urlEvidencia,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isSynced = isSynced
)