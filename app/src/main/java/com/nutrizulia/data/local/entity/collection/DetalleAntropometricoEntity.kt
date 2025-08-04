package com.nutrizulia.data.local.entity.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.data.remote.dto.collection.DetalleAntropometricoDto
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
    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP") val updatedAt: LocalDateTime,
    @ColumnInfo(name = "is_deleted", defaultValue = "0") val isDeleted: Boolean,
    @ColumnInfo(name = "is_synced", defaultValue = "0") val isSynced: Boolean
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
    isDeleted = isDeleted,
    isSynced = isSynced
)

fun DetalleAntropometricoEntity.toDto() = DetalleAntropometricoDto(
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