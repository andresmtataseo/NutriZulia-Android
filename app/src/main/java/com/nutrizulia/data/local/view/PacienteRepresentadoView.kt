package com.nutrizulia.data.local.view

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import java.time.LocalDate
import java.time.LocalDateTime

@DatabaseView(
    viewName = "pacientes_representados",
    value = """
        SELECT
            pr.id AS relacionId,
            pr.usuario_institucion_id AS usuarioInstitucionId,
            pr.representante_id AS representanteId,
            
            p.id AS pacienteId,
            p.cedula AS pacienteCedula,
            p.nombres AS pacienteNombres,
            p.apellidos AS pacienteApellidos,
            p.fecha_nacimiento AS pacienteFechaNacimiento,
            p.genero AS pacienteGenero,
            p.telefono AS pacienteTelefono,
            p.correo AS pacienteCorreo,
            
            pa.id AS parentescoId,
            pa.nombre AS parentescoNombre,
            
            pr.updated_at AS ultimaActualizacion
            
        FROM pacientes_representantes pr
        INNER JOIN pacientes p ON pr.paciente_id = p.id
        INNER JOIN parentescos pa ON pr.parentesco_id = pa.id
        WHERE pr.is_deleted = 0 AND p.is_deleted = 0
        ORDER BY pr.updated_at DESC
    """
)
data class PacienteRepresentadoView(
    // Información de la relación
    @ColumnInfo(name = "relacionId") val relacionId: String,
    @ColumnInfo(name = "usuarioInstitucionId") val usuarioInstitucionId: Int,
    @ColumnInfo(name = "representanteId") val representanteId: String,
    
    // Información del paciente
    @ColumnInfo(name = "pacienteId") val pacienteId: String,
    @ColumnInfo(name = "pacienteCedula") val pacienteCedula: String,
    @ColumnInfo(name = "pacienteNombres") val pacienteNombres: String,
    @ColumnInfo(name = "pacienteApellidos") val pacienteApellidos: String,
    @ColumnInfo(name = "pacienteFechaNacimiento") val pacienteFechaNacimiento: LocalDate,
    @ColumnInfo(name = "pacienteGenero") val pacienteGenero: String,
    @ColumnInfo(name = "pacienteTelefono") val pacienteTelefono: String?,
    @ColumnInfo(name = "pacienteCorreo") val pacienteCorreo: String?,
    
    // Información del parentesco
    @ColumnInfo(name = "parentescoId") val parentescoId: Int,
    @ColumnInfo(name = "parentescoNombre") val parentescoNombre: String,
    
    // Metadatos
    @ColumnInfo(name = "ultimaActualizacion") val ultimaActualizacion: LocalDateTime
)