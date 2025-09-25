package com.nutrizulia.data.local.pojo

import androidx.room.ColumnInfo
import java.time.LocalDateTime

data class DiagnosticoConDescripcion(
    val id: String,
    @ColumnInfo(name = "consulta_id")
    val consultaId: String,
    @ColumnInfo(name = "riesgo_biologico_id")
    val riesgoBiologicoId: Int,
    @ColumnInfo(name = "enfermedad_id")
    val enfermedadId: Int?,
    @ColumnInfo(name = "is_principal")
    val isPrincipal: Boolean,
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime,
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean,
    @ColumnInfo(name = "riesgo_biologico_nombre")
    val riesgoBiologicoNombre: String?,
    @ColumnInfo(name = "enfermedad_nombre")
    val enfermedadNombre: String?,
    @ColumnInfo(name = "fecha_consulta")
    val fechaConsulta: LocalDateTime
)