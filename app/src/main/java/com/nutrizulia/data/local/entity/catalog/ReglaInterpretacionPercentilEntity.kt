package com.nutrizulia.data.local.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.catalog.ReglaInterpretacionPercentil

@Entity(
    tableName = "reglas_interpretaciones_percentil",
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
data class ReglaInterpretacionPercentilEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "tipo_indicador_id") val tipoIndicadorId: Int,
    @ColumnInfo(name = "percentil_minimo") val percentilMinimo: Double?,
    @ColumnInfo(name = "percentil_maximo") val percentilMaximo: Double?,
    @ColumnInfo(name = "diagnostico") val diagnostico: String
)

fun ReglaInterpretacionPercentil.toEntity() = ReglaInterpretacionPercentilEntity(
    id = id,
    tipoIndicadorId = tipoIndicadorId,
    percentilMinimo = percentilMinimo,
    percentilMaximo = percentilMaximo,
    diagnostico = diagnostico
)