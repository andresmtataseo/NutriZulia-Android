package com.nutrizulia.data.local.entity.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.data.local.entity.catalog.EspecialidadEntity
import com.nutrizulia.data.local.entity.catalog.TipoActividadEntity
import com.nutrizulia.data.local.entity.user.UsuarioInstitucionEntity
import com.nutrizulia.data.local.enum.Estado
import com.nutrizulia.data.local.enum.TipoConsulta
import com.nutrizulia.domain.model.collection.Consulta
import java.time.LocalDateTime

@Entity(
    tableName = "consultas",
    indices = [
        Index(value = ["usuario_institucion_id"]),
        Index(value = ["paciente_id"]),
        Index(value = ["tipo_actividad_id"]),
        Index(value = ["especialidad_remitente_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UsuarioInstitucionEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuario_institucion_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = PacienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["paciente_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = TipoActividadEntity::class,
            parentColumns = ["id"],
            childColumns = ["tipo_actividad_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = EspecialidadEntity::class,
            parentColumns = ["id"],
            childColumns = ["especialidad_remitente_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class ConsultaEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "usuario_institucion_id") val usuarioInstitucionId: Int,
    @ColumnInfo(name = "paciente_id") val pacienteId: String,
    @ColumnInfo(name = "tipo_actividad_id") val tipoActividadId: Int,
    @ColumnInfo(name = "especialidad_remitente_id") val especialidadRemitenteId: Int,
    @ColumnInfo(name = "tipo_consulta") val tipoConsulta: TipoConsulta,
    @ColumnInfo(name = "motivo_consulta") val motivoConsulta: String?,
    @ColumnInfo(name = "fecha_hora_programada") val fechaHoraProgramada: LocalDateTime?,
    @ColumnInfo(name = "observaciones") val observaciones: String?,
    @ColumnInfo(name = "planes") val planes: String?,
    @ColumnInfo(name = "fecha_hora_real") val fechaHoraReal: LocalDateTime?,
    @ColumnInfo(name = "estado") val estado: Estado,
    @ColumnInfo(name = "updated_at") val updatedAt: LocalDateTime,
)

fun Consulta.toEntity() = ConsultaEntity(
    id = id,
    usuarioInstitucionId = usuarioInstitucionId,
    pacienteId = pacienteId,
    tipoActividadId = tipoActividadId,
    especialidadRemitenteId = especialidadRemitenteId,
    tipoConsulta = tipoConsulta ?: TipoConsulta.PRIMERA_CONSULTA,
    motivoConsulta = motivoConsulta,
    fechaHoraProgramada = fechaHoraProgramada,
    observaciones = observaciones,
    planes = planes,
    fechaHoraReal = fechaHoraReal,
    estado = estado,
    updatedAt = updatedAt
)