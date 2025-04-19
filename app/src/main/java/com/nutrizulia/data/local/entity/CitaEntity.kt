package com.nutrizulia.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.Cita

@Entity(
    tableName = "citas",
    indices = [
        Index(value = ["usuario_id"]),
        Index(value = ["paciente_id"])
    ],
    foreignKeys = [ForeignKey(
        entity = UsuarioEntity::class,
        parentColumns = ["id"],
        childColumns = ["usuario_id"],
        onDelete = ForeignKey.NO_ACTION
    ), ForeignKey(
        entity = PacienteEntity::class,
        parentColumns = ["id"],
        childColumns = ["paciente_id"],
        onDelete = ForeignKey.NO_ACTION
    )]
)
data class CitaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "usuario_id") val usuarioId: Int,
    @ColumnInfo(name = "paciente_id") val pacienteId: Int,
    @ColumnInfo(name = "tipo_cita") val tipoCita: String,
    @ColumnInfo(name = "especialidad") val especialidad: String,
    @ColumnInfo(name = "motivo_cita") val motivoCita: String,
    @ColumnInfo(name = "fecha_programada") val fechaProgramada: String,
    @ColumnInfo(name = "hora_programada") val horaProgramada: String,
    @ColumnInfo(name = "estado") val estado: String
)

fun Cita.toEntity() = CitaEntity(
    usuarioId = usuarioId,
    pacienteId = pacienteId,
    tipoCita = tipoCita,
    especialidad = especialidad,
    motivoCita = motivoCita,
    fechaProgramada = fechaProgramada,
    horaProgramada = horaProgramada,
    estado = estado
)