package com.nutrizulia.data.local.entity.catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.catalog.ReglaInterpretacionImc

@Entity(
    tableName = "reglas_interpretaciones_imc",
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
data class ReglaInterpretacionImcEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "tipo_indicador_id") val tipoIndicadorId: Int,
    @ColumnInfo(name = "imc_minimo") val imcMinimo: Double?,
    @ColumnInfo(name = "imc_maximo") val imcMaximo: Double?,
    @ColumnInfo(name = "diagnostico") val diagnostico: String
)

fun ReglaInterpretacionImc.toEntity() = ReglaInterpretacionImcEntity(
    id = id,
    tipoIndicadorId = tipoIndicadorId,
    imcMinimo = imcMinimo,
    imcMaximo = imcMaximo,
    diagnostico = diagnostico
)