package com.nutrizulia.data.local.entity.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.data.local.entity.catalog.EnfermedadEntity
import com.nutrizulia.data.local.entity.catalog.RiesgoBiologicoEntity
import com.nutrizulia.domain.model.collection.Diagnostico
import java.time.LocalDateTime

@Entity(
    tableName = "diagnosticos",
    indices = [
        Index(value = ["consulta_id"]),
        Index(value = ["riesgo_biologico_id"]),
        Index(value = ["enfermedad_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = ConsultaEntity::class,
            parentColumns = ["id"],
            childColumns = ["consulta_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = RiesgoBiologicoEntity::class,
            parentColumns = ["id"],
            childColumns = ["riesgo_biologico_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = EnfermedadEntity::class,
            parentColumns = ["id"],
            childColumns = ["enfermedad_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class DiagnosticoEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "consulta_id") val consultaId: String,
    @ColumnInfo(name = "riesgo_biologico_id") val riesgoBiologicoId: Int,
    @ColumnInfo(name = "enfermedad_id") val enfermedadId: Int?,
    @ColumnInfo(name = "is_principal") val isPrincipal: Boolean,
    @ColumnInfo(name = "updated_at") val updatedAt: LocalDateTime
)

fun Diagnostico.toEntity() = DiagnosticoEntity(
    id = id,
    consultaId = consultaId,
    riesgoBiologicoId = riesgoBiologicoId,
    enfermedadId = enfermedadId,
    isPrincipal = isPrincipal,
    updatedAt = updatedAt
)