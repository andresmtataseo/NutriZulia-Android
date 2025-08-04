package com.nutrizulia.data.local.entity.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.data.local.entity.catalog.TipoIndicadorEntity
import com.nutrizulia.data.local.enum.TipoValorCalculado
import com.nutrizulia.data.remote.dto.collection.EvaluacionAntropometricaDto
import com.nutrizulia.domain.model.collection.EvaluacionAntropometrica
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "evaluaciones_antropometricas",
    indices = [
        Index(value = ["consulta_id"]),
        Index(value = ["detalle_antropometrico_id"]),
        Index(value = ["tipo_indicador_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = ConsultaEntity::class,
            parentColumns = ["id"],
            childColumns = ["consulta_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = DetalleAntropometricoEntity::class,
            parentColumns = ["id"],
            childColumns = ["detalle_antropometrico_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity =  TipoIndicadorEntity::class,
            parentColumns = ["id"],
            childColumns = ["tipo_indicador_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
    ]
)
data class EvaluacionAntropometricaEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "consulta_id") val consultaId: String,
    @ColumnInfo(name = "detalle_antropometrico_id") val detalleAntropometricoId: String,
    @ColumnInfo(name = "tipo_indicador_id") val tipoIndicadorId: Int,
    @ColumnInfo(name = "valor_calculado") val valorCalculado: Double,
    @ColumnInfo(name = "tipo_valor_calculado") val tipoValorCalculado: TipoValorCalculado,
    @ColumnInfo(name = "diagnostico_antropometrico") val diagnosticoAntropometrico: String,
    @ColumnInfo(name = "fecha_evaluacion") val fechaEvaluacion: LocalDate,
    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP") val updatedAt: LocalDateTime,
    @ColumnInfo(name = "is_deleted", defaultValue = "0") val isDeleted: Boolean,
    @ColumnInfo(name = "is_synced", defaultValue = "0") val isSynced: Boolean
)

fun EvaluacionAntropometrica.toEntity() = EvaluacionAntropometricaEntity(
    id = id,
    consultaId = consultaId,
    detalleAntropometricoId = detalleAntropometricoId,
    tipoIndicadorId = tipoIndicadorId,
    valorCalculado = valorCalculado,
    tipoValorCalculado = tipoValorCalculado,
    diagnosticoAntropometrico = diagnosticoAntropometrico,
    fechaEvaluacion = fechaEvaluacion,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isSynced = isSynced
)

fun EvaluacionAntropometricaEntity.toDto() = EvaluacionAntropometricaDto(
    id = id,
    consultaId = consultaId,
    detalleAntropometricoId = detalleAntropometricoId,
    tipoIndicadorId = tipoIndicadorId,
    valorCalculado = valorCalculado,
    tipoValorCalculado = tipoValorCalculado,
    diagnosticoAntropometrico = diagnosticoAntropometrico,
    fechaEvaluacion = fechaEvaluacion,
    updatedAt = updatedAt,
    isDeleted = isDeleted
)