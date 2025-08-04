package com.nutrizulia.data.local.entity.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.data.remote.dto.collection.DetalleObstetriciaDto
import com.nutrizulia.domain.model.collection.DetalleObstetricia
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "detalles_obstetricias",
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
data class DetalleObstetriciaEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "consulta_id") val consultaId: String,
    @ColumnInfo(name = "esta_embarazada") val estaEmbarazada: Boolean?,
    @ColumnInfo(name = "fecha_ultima_menstruacion") val fechaUltimaMenstruacion: LocalDate?,
    @ColumnInfo(name = "semanas_gestacion") val semanasGestacion: Int?,
    @ColumnInfo(name = "peso_pre_embarazo") val pesoPreEmbarazo: Double?,
    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP") val updatedAt: LocalDateTime,
    @ColumnInfo(name = "is_deleted", defaultValue = "0") val isDeleted: Boolean,
    @ColumnInfo(name = "is_synced", defaultValue = "0") val isSynced: Boolean
)

fun DetalleObstetricia.toEntity() = DetalleObstetriciaEntity(
    id = id,
    consultaId = consultaId,
    estaEmbarazada = estaEmbarazada,
    fechaUltimaMenstruacion = fechaUltimaMenstruacion,
    semanasGestacion = semanasGestacion,
    pesoPreEmbarazo = pesoPreEmbarazo,
    updatedAt = updatedAt,
    isDeleted = isDeleted,
    isSynced = isSynced
)

fun DetalleObstetriciaEntity.toDto() = DetalleObstetriciaDto(
    id = id,
    consultaId = consultaId,
    estaEmbarazada = estaEmbarazada,
    fechaUltimaMenstruacion = fechaUltimaMenstruacion,
    semanasGestacion = semanasGestacion,
    pesoPreEmbarazo = pesoPreEmbarazo,
    updatedAt = updatedAt,
    isDeleted = isDeleted
)