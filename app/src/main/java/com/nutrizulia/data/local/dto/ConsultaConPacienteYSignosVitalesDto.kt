package com.nutrizulia.data.local.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.nutrizulia.data.local.entity.ConsultaEntity
import com.nutrizulia.data.local.entity.PacienteEntity
import com.nutrizulia.data.local.entity.SignosVitalesEntity

data class ConsultaConPacienteYSignosVitalesDto(
// La entidad principal de este DTO
    @Embedded val consulta: ConsultaEntity,

    // Relación para obtener el Paciente asociado a esta Consulta
    @Relation(
        parentColumn = "paciente_id", // Columna en la entidad principal (ConsultaEntity) que tiene la FK
        entityColumn = "id"          // Columna en la entidad relacionada (PacienteEntity) que es la PK referenciada
    )
    val paciente: PacienteEntity, // Una consulta tiene un paciente

    // Relación para obtener los Signos Vitales asociados a esta Consulta
    @Relation(
        parentColumn = "id",       // Columna en la entidad principal (ConsultaEntity) que es la PK (el ID de la Consulta)
        entityColumn = "consulta_id" // Columna en la entidad relacionada (SignosVitalesEntity) que tiene la FK a Consulta
    )
    // Usamos SignosVitalesEntity? (nullable) porque una Consulta podría no tener un registro
    // de Signos Vitales asociado aún (la relación es opcional o 1:0..1)
    val signosVitales: SignosVitalesEntity // Una consulta puede tener (opcionalmente) un registro de signos vitales
)