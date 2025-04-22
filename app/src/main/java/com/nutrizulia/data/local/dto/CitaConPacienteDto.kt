package com.nutrizulia.data.local.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.nutrizulia.data.local.entity.CitaEntity
import com.nutrizulia.data.local.entity.PacienteEntity

data class CitaConPacienteDto(
    @Embedded val cita: CitaEntity,

    @Relation(
        parentColumn = "paciente_id",
        entityColumn = "id"
    )
    val paciente: PacienteEntity
)
