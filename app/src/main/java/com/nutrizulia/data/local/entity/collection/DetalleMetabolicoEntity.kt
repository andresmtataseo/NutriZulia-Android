package com.nutrizulia.data.local.entity.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.collection.DetalleMetabolico
import java.time.LocalDateTime

@Entity(
    tableName = "detalles_metabolicos",
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
data class DetalleMetabolicoEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "consulta_id") val consultaId: String,
    @ColumnInfo(name = "glicemia_basal") val glicemiaBasal: Int?,
    @ColumnInfo(name = "glicemia_postprandial") val glicemiaPostprandial: Int?,
    @ColumnInfo(name = "glicemia_aleatoria") val glicemiaAleatoria: Int?,
    @ColumnInfo(name = "hemoglobina_glicosilada") val hemoglobinaGlicosilada: Double?,
    @ColumnInfo(name = "trigliceridos") val trigliceridos: Int?,
    @ColumnInfo(name = "colesterol_total") val colesterolTotal: Int?,
    @ColumnInfo(name = "colesterol_hdl") val colesterolHdl: Int?,
    @ColumnInfo(name = "colesterol_ldl") val colesterolLdl: Int?,
    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP") val updatedAt: LocalDateTime,
    @ColumnInfo(name = "is_deleted", defaultValue = "0") val isDeleted: Boolean,
    @ColumnInfo(name = "is_synced", defaultValue = "0") val isSynced: Boolean
)

fun DetalleMetabolico.toEntity() = DetalleMetabolicoEntity(
    id = id,
    consultaId = consultaId,
    glicemiaBasal = glicemiaBasal,
    glicemiaPostprandial = glicemiaPostprandial,
    glicemiaAleatoria = glicemiaAleatoria,
    hemoglobinaGlicosilada = hemoglobinaGlicosilada,
    trigliceridos = trigliceridos,
    colesterolTotal = colesterolTotal,
    colesterolHdl = colesterolHdl,
    colesterolLdl = colesterolLdl,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isSynced = isSynced
)