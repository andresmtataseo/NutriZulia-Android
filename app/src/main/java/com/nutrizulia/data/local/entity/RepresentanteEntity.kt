package com.nutrizulia.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.domain.model.Representante

@Entity(
    tableName = "representantes",
    indices = [
        Index(value = ["paciente_id"]),
        Index(value = ["representante_id"])
    ],
    foreignKeys = [ForeignKey(
        entity = PacienteEntity::class,
        parentColumns = ["id"],
        childColumns = ["paciente_id"],
        onDelete = ForeignKey.NO_ACTION
    ), ForeignKey(
        entity = PacienteEntity::class,
        parentColumns = ["id"],
        childColumns = ["representante_id"],
        onDelete = ForeignKey.NO_ACTION
    )]
)
data class RepresentanteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "parentesco") val parentesco: String,
    @ColumnInfo(name = "paciente_id") val pacienteId: Int,
    @ColumnInfo(name = "representante_id") val representanteId: Int
)

fun Representante.toEntity() = RepresentanteEntity(
    parentesco = parentesco,
    pacienteId = pacienteId,
    representanteId = representanteId
)