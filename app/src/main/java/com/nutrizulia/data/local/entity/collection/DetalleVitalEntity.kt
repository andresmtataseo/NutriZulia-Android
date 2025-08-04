package com.nutrizulia.data.local.entity.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.data.remote.dto.collection.DetalleVitalDto
import com.nutrizulia.domain.model.collection.DetalleVital
import java.time.LocalDateTime

@Entity(
    tableName = "detalles_vitales",
    indices = [
        Index(value = ["consulta_id"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = ConsultaEntity::class,
            parentColumns = ["id"],
            childColumns = ["consulta_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class DetalleVitalEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "consulta_id") val consultaId: String,
    @ColumnInfo(name = "tension_arterial_sistolica") val tensionArterialSistolica: Int?,
    @ColumnInfo(name = "tension_arterial_diastolica") val tensionArterialDiastolica: Int?,
    @ColumnInfo(name = "frecuencia_cardiaca") val frecuenciaCardiaca: Int?,
    @ColumnInfo(name = "frecuencia_respiratoria") val frecuenciaRespiratoria: Int?,
    @ColumnInfo(name = "temperatura") val temperatura: Double?,
    @ColumnInfo(name = "saturacion_oxigeno") val saturacionOxigeno: Int?,
    @ColumnInfo(name = "pulso") val pulso: Int?,
    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP") val updatedAt: LocalDateTime,
    @ColumnInfo(name = "is_deleted", defaultValue = "0") val isDeleted: Boolean,
    @ColumnInfo(name = "is_synced", defaultValue = "0") val isSynced: Boolean
)

fun DetalleVital.toEntity() = DetalleVitalEntity(
    id = id,
    consultaId = consultaId,
    tensionArterialSistolica = tensionArterialSistolica,
    tensionArterialDiastolica = tensionArterialDiastolica,
    frecuenciaCardiaca = frecuenciaCardiaca,
    frecuenciaRespiratoria = frecuenciaRespiratoria,
    temperatura = temperatura,
    saturacionOxigeno = saturacionOxigeno,
    pulso = pulso,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isSynced = isSynced
)

fun DetalleVitalEntity.toDto() = DetalleVitalDto(
    id = id,
    consultaId = consultaId,
    tensionArterialSistolica = tensionArterialSistolica,
    tensionArterialDiastolica = tensionArterialDiastolica,
    frecuenciaCardiaca = frecuenciaCardiaca,
    frecuenciaRespiratoria = frecuenciaRespiratoria,
    temperatura = temperatura,
    saturacionOxigeno = saturacionOxigeno,
    pulso = pulso,
    updatedAt = updatedAt,
    isDeleted = isDeleted
)