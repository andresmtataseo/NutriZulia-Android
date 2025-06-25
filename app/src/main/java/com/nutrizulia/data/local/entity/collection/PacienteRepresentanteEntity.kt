package com.nutrizulia.data.local.entity.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nutrizulia.data.local.entity.catalog.ParentescoEntity
import com.nutrizulia.data.local.entity.user.UsuarioInstitucionEntity
import com.nutrizulia.domain.model.collection.PacienteRepresentante
import java.time.LocalDateTime

@Entity(
    tableName = "pacientes_representantes",
    indices = [
        Index(value = ["usuario_institucion_id"]),
        Index(value = ["paciente_id"]),
        Index(value = ["representante_id"]),
        Index(value = ["parentesco_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UsuarioInstitucionEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuario_institucion_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = PacienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["paciente_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = RepresentanteEntity::class,
            parentColumns = ["id"],
            childColumns = ["representante_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = ParentescoEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentesco_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
    ]
)
data class PacienteRepresentanteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "usuario_institucion_id") val usuarioInstitucionId: Int,
    @ColumnInfo(name = "paciente_id") val pacienteId: String,
    @ColumnInfo(name = "representante_id") val representanteId: String,
    @ColumnInfo(name = "parentesco_id") val parentescoId: Int,
    @ColumnInfo(name = "updated_at") val updatedAt: LocalDateTime,
)

fun PacienteRepresentante.toEntity() = PacienteRepresentanteEntity(
    id = id,
    usuarioInstitucionId = usuarioInstitucionId,
    pacienteId = pacienteId,
    representanteId = representanteId,
    parentescoId = parentescoId,
    updatedAt = updatedAt
)