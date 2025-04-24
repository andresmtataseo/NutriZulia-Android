package com.nutrizulia.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.Consulta
import com.nutrizulia.util.Utils.obtenerFechaActual
import com.nutrizulia.util.Utils.obtenerHoraActual

@Entity(
    tableName = "consultas",
    indices = [
        Index(value = ["usuario_id"]),
        Index(value = ["paciente_id"]),
        Index(value = ["cita_id"]),
        Index(value = ["actividad_id"])
    ],
    foreignKeys = [ForeignKey(
        entity = UsuarioEntity::class,
        parentColumns = ["id"],
        childColumns = ["usuario_id"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = PacienteEntity::class,
        parentColumns = ["id"],
        childColumns = ["paciente_id"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = CitaEntity::class,
        parentColumns = ["id"],
        childColumns = ["cita_id"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = ActividadEntity::class,
        parentColumns = ["id"],
        childColumns = ["actividad_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ConsultaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "usuario_id") val usuarioId: Int,
    @ColumnInfo(name = "paciente_id") val pacienteId: Int,
    @ColumnInfo(name = "cita_id") val citaId: Int?,
    @ColumnInfo(name = "actividad_id") val actividadId: Int?,
    @ColumnInfo(name = "fecha") val fecha: String = obtenerFechaActual(),
    @ColumnInfo(name = "hora") val hora: String = obtenerHoraActual(),
    @ColumnInfo(name = "diagnostico_principal") val diagnosticoPrincipal: String?,
    @ColumnInfo(name = "diagnostico_secundario") val diagnosticoSecundario: String?,
    @ColumnInfo(name = "observaciones") val observaciones: String?
)

fun Consulta.toEntity() = ConsultaEntity(
    usuarioId = usuarioId,
    pacienteId = pacienteId,
    citaId = citaId,
    actividadId = actividadId,
    fecha = fecha,
    hora = hora,
    diagnosticoPrincipal = diagnosticoPrincipal,
    diagnosticoSecundario = diagnosticoSecundario,
    observaciones = observaciones
)