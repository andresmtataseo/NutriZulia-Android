package com.nutrizulia.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nutrizulia.data.local.converter.*
import com.nutrizulia.data.local.dao.*
import com.nutrizulia.data.local.entity.catalog.*
import com.nutrizulia.data.local.entity.collection.*
import com.nutrizulia.data.local.entity.user.*
import com.nutrizulia.data.local.view.*
import com.nutrizulia.data.local.dao.catalog.*
import com.nutrizulia.data.local.dao.collection.*
import com.nutrizulia.data.local.dao.user.*


@Database(
    entities = [
        // Catalog
        EnfermedadEntity::class,
        EspecialidadEntity::class,
        EstadoEntity::class,
        EtniaEntity::class,
        GrupoEtarioEntity::class,
        MunicipioEntity::class,
        MunicipioSanitarioEntity::class,
        NacionalidadEntity::class,
        ParametroCrecimientoNinoEdadEntity::class,
        ParametroCrecimientoPediatricoEdadEntity::class,
        ParametroCrecimientoPediatricoLongitudEntity::class,
        ParentescoEntity::class,
        ParroquiaEntity::class,
        RegexEntity::class,
        ReglaInterpretacionImcEntity::class,
        ReglaInterpretacionPercentilEntity::class,
        ReglaInterpretacionZScoreEntity::class,
        RiesgoBiologicoEntity::class,
        TipoActividadEntity::class,
        TipoIndicadorEntity::class,
        TipoInstitucionEntity::class,
        VersionEntity::class,
        // User
        UsuarioEntity::class,
        UsuarioInstitucionEntity::class,
        RolEntity::class,
        InstitucionEntity::class,
        // Collection
        ActividadEntity::class,
        ConsultaEntity::class,
        DetalleAntropometricoEntity::class,
        DetalleMetabolicoEntity::class,
        DetalleObstetriciaEntity::class,
        DetallePediatricoEntity::class,
        DetalleVitalEntity::class,
        DiagnosticoEntity::class,
        EvaluacionAntropometricaEntity::class,
        PacienteEntity::class,
        PacienteRepresentanteEntity::class,
        RepresentanteEntity::class
    ],
    views = [
        PacienteConCita::class,
        PerfilInstitucional::class,
        ActividadConTipo::class,
        PacienteRepresentadoView::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    DateConverters::class,
    EnumConverters::class
)
abstract class AppDatabase : RoomDatabase() {

    // DAOs para Entidades (Catalog)
    abstract fun enfermedadDao(): EnfermedadDao
    abstract fun especialidadDao(): EspecialidadDao
    abstract fun estadoDao(): EstadoDao
    abstract fun etniaDao(): EtniaDao
    abstract fun grupoEtarioDao(): GrupoEtarioDao
    abstract fun municipioDao(): MunicipioDao
    abstract fun municipioSanitarioDao(): MunicipioSanitarioDao
    abstract fun nacionalidadDao(): NacionalidadDao
    abstract fun parametroCrecimientoNinoEdadDao(): ParametroCrecimientoNinoEdadDao
    abstract fun parametroCrecimientoPediatricoEdadDao(): ParametroCrecimientoPediatricoEdadDao
    abstract fun parametroCrecimientoPediatricoLongitudDao(): ParametroCrecimientoPediatricoLongitudDao
    abstract fun parentescoDao(): ParentescoDao
    abstract fun parroquiaDao(): ParroquiaDao
    abstract fun regexDao(): RegexDao
    abstract fun reglaInterpretacionImcDao(): ReglaInterpretacionImcDao
    abstract fun reglaInterpretacionPercentilDao(): ReglaInterpretacionPercentilDao
    abstract fun reglaInterpretacionZScoreDao(): ReglaInterpretacionZScoreDao
    abstract fun riesgoBiologicoDao(): RiesgoBiologicoDao
    abstract fun tipoActividadDao(): TipoActividadDao
    abstract fun tipoIndicadorDao(): TipoIndicadorDao
    abstract fun tipoInstitucionDao(): TipoInstitucionDao
    abstract fun versionDao(): VersionDao

    // DAOs para Entidades (User)
    abstract fun usuarioDao(): UsuarioDao
    abstract fun usuarioInstitucionDao(): UsuarioInstitucionDao
    abstract fun rolDao(): RolDao
    abstract fun institucionDao(): InstitucionDao

    // DAOs para Entidades (Collection)
    abstract fun actividadDao(): ActividadDao
    abstract fun consultaDao(): ConsultaDao
    abstract fun detalleAntropometricoDao(): DetalleAntropometricoDao
    abstract fun detalleMetabolicoDao(): DetalleMetabolicoDao
    abstract fun detalleObstetriciaDao(): DetalleObstetriciaDao
    abstract fun detallePediatricoDao(): DetallePediatricoDao
    abstract fun detalleVitalDao(): DetalleVitalDao
    abstract fun diagnosticoDao(): DiagnosticoDao
    abstract fun evaluacionAntropometricaDao(): EvaluacionAntropometricaDao
    abstract fun pacienteDao(): PacienteDao
    abstract fun pacienteRepresentanteDao(): PacienteRepresentanteDao
    abstract fun representanteDao(): RepresentanteDao

    // DAOs para Vistas
    abstract fun pacienteConCitaDao(): PacienteConCitaDao
    abstract fun perfilInstitucionalDao(): PerfilInstitucionalDao
    abstract fun pacienteRepresentadoDao(): PacienteRepresentadoDao

}