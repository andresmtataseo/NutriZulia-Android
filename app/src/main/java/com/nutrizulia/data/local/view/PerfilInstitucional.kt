package com.nutrizulia.data.local.view

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import java.time.LocalDate

@DatabaseView(
    value = """
        SELECT 
            ui.id AS usuario_institucion_id,
            ui.usuario_id,
            ui.rol_id,
            r.nombre AS rol_nombre,
            ui.institucion_id,
            i.nombre AS institucion_nombre,
            m.nombre AS municipio_nombre,
            t.nombre AS tipo_institucion_nombre,
            ui.fecha_inicio,
            ui.fecha_fin,
            ui.is_enabled
        FROM usuarios_instituciones AS ui
        INNER JOIN roles AS r ON ui.rol_id = r.id
        INNER JOIN instituciones AS i ON ui.institucion_id = i.id
        INNER JOIN municipios_sanitarios AS m ON i.municipio_sanitario_id = m.id
        INNER JOIN tipos_instituciones AS t ON i.tipo_institucion_id = t.id
    """,
    viewName = "perfiles_institucionales"
)
data class PerfilInstitucional(
    @ColumnInfo(name = "usuario_institucion_id")
    val usuarioInstitucionId: Int,

    @ColumnInfo(name = "usuario_id")
    val usuarioId: Int,

    @ColumnInfo(name = "rol_id")
    val rolId: Int,

    @ColumnInfo(name = "rol_nombre")
    val rolNombre: String,

    @ColumnInfo(name = "institucion_id")
    val institucionId: Int,

    @ColumnInfo(name = "institucion_nombre")
    val institucionNombre: String,

    @ColumnInfo(name = "municipio_nombre")
    val municipioNombre: String,

    @ColumnInfo(name = "tipo_institucion_nombre")
    val tipoInstitucionNombre: String,

    @ColumnInfo(name = "fecha_inicio")
    val fechaInicio: LocalDate,

    @ColumnInfo(name = "fecha_fin")
    val fechaFin: LocalDate?,

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean
)