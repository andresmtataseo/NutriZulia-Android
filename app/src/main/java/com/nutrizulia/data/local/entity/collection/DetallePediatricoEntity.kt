package com.nutrizulia.data.local.entity.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.data.local.enum.TipoLactancia
import com.nutrizulia.domain.model.collection.DetallePediatrico
import java.time.LocalDateTime

@Entity(
    tableName = "detalles_pediatricos",
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
data class DetallePediatricoEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "consulta_id") val consultaId: String,
    @ColumnInfo(name = "usa_biberon") val usaBiberon: Boolean?,
    @ColumnInfo(name = "tipo_lactancia") val tipoLactancia: TipoLactancia?,
    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP") val updatedAt: LocalDateTime,
    @ColumnInfo(name = "is_deleted", defaultValue = "0") val isDeleted: Boolean,
    @ColumnInfo(name = "is_synced", defaultValue = "0") val isSynced: Boolean
)

fun DetallePediatrico.toEntity() = DetallePediatricoEntity(
    id = id,
    consultaId = consultaId,
    usaBiberon = usaBiberon,
    tipoLactancia = tipoLactancia,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isSynced = isSynced
)