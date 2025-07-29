package com.nutrizulia.data.local.entity.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.collection.DetalleAntropometrico
import java.time.LocalDateTime

@Entity(
    tableName = "detalles_antropometricos",
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
data class DetalleAntropometricoEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "consulta_id") val consultaId: String,
    @ColumnInfo(name = "peso") val peso: Double?,
    @ColumnInfo(name = "altura") val altura: Double?,
    @ColumnInfo(name = "talla") val talla: Double?,
    @ColumnInfo(name = "circunferencia_braquial") val circunferenciaBraquial: Double?,
    @ColumnInfo(name = "circunferencia_cadera") val circunferenciaCadera: Double?,
    @ColumnInfo(name = "circunferencia_cintura") val circunferenciaCintura: Double?,
    @ColumnInfo(name = "perimetro_cefalico") val perimetroCefalico: Double?,
    @ColumnInfo(name = "pliegue_tricipital") val pliegueTricipital: Double?,
    @ColumnInfo(name = "pliegue_subescapular") val pliegueSubescapular: Double?,
    @ColumnInfo(name = "updated_at") val updatedAt: LocalDateTime,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false
)

fun DetalleAntropometrico.toEntity() = DetalleAntropometricoEntity(
    id = id,
    consultaId = consultaId,
    peso = peso,
    altura = altura,
    talla = talla,
    circunferenciaBraquial = circunferenciaBraquial,
    circunferenciaCadera = circunferenciaCadera,
    circunferenciaCintura = circunferenciaCintura,
    perimetroCefalico = perimetroCefalico,
    pliegueTricipital = pliegueTricipital,
    pliegueSubescapular = pliegueSubescapular,
    updatedAt = updatedAt,
    isDeleted = isDeleted
)