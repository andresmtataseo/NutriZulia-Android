package com.nutrizulia.data.local.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.catalog.ReglaInterpretacionZScore

@Entity(
    tableName = "reglas_interpretaciones_z_score",
    indices = [
        Index(value = ["tipo_indicador_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = TipoIndicadorEntity::class,
            parentColumns = ["id"],
            childColumns = ["tipo_indicador_id"],
            onDelete = ForeignKey.NO_ACTION),
    ]
)
data class ReglaInterpretacionZScoreEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "tipo_indicador_id") val tipoIndicadorId: Int,
    @ColumnInfo(name = "z_score_minimo") val zScoreMinimo: Double?,
    @ColumnInfo(name = "z_score_maximo") val zScoreMaximo: Double?,
    @ColumnInfo(name = "diagnostico") val diagnostico: String
)

fun ReglaInterpretacionZScore.toEntity() = ReglaInterpretacionZScoreEntity(
    id = id,
    tipoIndicadorId = tipoIndicadorId,
    zScoreMinimo = zScoreMinimo,
    zScoreMaximo = zScoreMaximo,
    diagnostico = diagnostico
)