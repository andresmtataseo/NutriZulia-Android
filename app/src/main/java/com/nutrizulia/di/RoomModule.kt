package com.nutrizulia.di

import android.content.Context
import androidx.room.Room
import com.nutrizulia.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    private const val DATABASE_NAME = "nutrizulia_db"

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration(true).build()
    }

    // DAOs para Entidades (Catalog)
    @Singleton
    @Provides
    fun provideEnfermedadDao(database: AppDatabase) = database.enfermedadDao()

    @Singleton
    @Provides
    fun provideEspecialidadDao(database: AppDatabase) = database.especialidadDao()

    @Singleton
    @Provides
    fun provideEstadoDao(database: AppDatabase) = database.estadoDao()

    @Singleton
    @Provides
    fun provideEtniaDao(database: AppDatabase) = database.etniaDao()

    @Singleton
    @Provides
    fun provideGrupoEtarioDao(database: AppDatabase) = database.grupoEtarioDao()

    @Singleton
    @Provides
    fun provideMunicipioDao(database: AppDatabase) = database.municipioDao()

    @Singleton
    @Provides
    fun provideMunicipioSanitarioDao(database: AppDatabase) = database.municipioSanitarioDao()

    @Singleton
    @Provides
    fun provideNacionalidadDao(database: AppDatabase) = database.nacionalidadDao()

    @Singleton
    @Provides
    fun provideParametroCrecimientoNinoEdadDao(database: AppDatabase) = database.parametroCrecimientoNinoEdadDao()

    @Singleton
    @Provides
    fun provideParametroCrecimientoPediatricoEdadDao(database: AppDatabase) = database.parametroCrecimientoPediatricoEdadDao()

    @Singleton
    @Provides
    fun provideParametroCrecimientoPediatricoLongitudDao(database: AppDatabase) = database.parametroCrecimientoPediatricoLongitudDao()

    @Singleton
    @Provides
    fun provideParentescoDao(database: AppDatabase) = database.parentescoDao()

    @Singleton
    @Provides
    fun provideParroquiaDao(database: AppDatabase) = database.parroquiaDao()

    @Singleton
    @Provides
    fun provideRegexDao(database: AppDatabase) = database.regexDao()

    @Singleton
    @Provides
    fun provideReglaInterpretacionImcDao(database: AppDatabase) = database.reglaInterpretacionImcDao()

    @Singleton
    @Provides
    fun provideReglaInterpretacionPercentilDao(database: AppDatabase) = database.reglaInterpretacionPercentilDao()

    @Singleton
    @Provides
    fun provideReglaInterpretacionZScoreDao(database: AppDatabase) = database.reglaInterpretacionZScoreDao()

    @Singleton
    @Provides
    fun provideRiesgoBiologicoDao(database: AppDatabase) = database.riesgoBiologicoDao()

    @Singleton
    @Provides
    fun provideTipoActividadDao(database: AppDatabase) = database.tipoActividadDao()

    @Singleton
    @Provides
    fun provideTipoIndicadorDao(database: AppDatabase) = database.tipoIndicadorDao()

    @Singleton
    @Provides
    fun provideTipoInstitucionDao(database: AppDatabase) = database.tipoInstitucionDao()

    @Singleton
    @Provides
    fun provideVersionDao(database: AppDatabase) = database.versionDao()

    // DAOs para Entidades (User)
    @Singleton
    @Provides
    fun provideUsuarioDao(database: AppDatabase) = database.usuarioDao()

    @Singleton
    @Provides
    fun provideUsuarioInstitucionDao(database: AppDatabase) = database.usuarioInstitucionDao()

    @Singleton
    @Provides
    fun provideRolDao(database: AppDatabase) = database.rolDao()

    @Singleton
    @Provides
    fun provideInstitucionDao(database: AppDatabase) = database.institucionDao()

    // DAOs para Entidades (Collection)
    @Singleton
    @Provides
    fun provideActividadDao(database: AppDatabase) = database.actividadDao()

    @Singleton
    @Provides
    fun provideConsultaDao(database: AppDatabase) = database.consultaDao()

    @Singleton
    @Provides
    fun provideDetalleAntropometricoDao(database: AppDatabase) = database.detalleAntropometricoDao()

    @Singleton
    @Provides
    fun provideDetalleMetabolicoDao(database: AppDatabase) = database.detalleMetabolicoDao()

    @Singleton
    @Provides
    fun provideDetalleObstetriciaDao(database: AppDatabase) = database.detalleObstetriciaDao()

    @Singleton
    @Provides
    fun provideDetallePediatricoDao(database: AppDatabase) = database.detallePediatricoDao()

    @Singleton
    @Provides
    fun provideDetalleVitalDao(database: AppDatabase) = database.detalleVitalDao()

    @Singleton
    @Provides
    fun provideDiagnosticoDao(database: AppDatabase) = database.diagnosticoDao()

    @Singleton
    @Provides
    fun provideEvaluacionAntropometricaDao(database: AppDatabase) = database.evaluacionAntropometricaDao()

    @Singleton
    @Provides
    fun providePacienteDao(database: AppDatabase) = database.pacienteDao()

    @Singleton
    @Provides
    fun providePacienteRepresentanteDao(database: AppDatabase) = database.pacienteRepresentanteDao()

    @Singleton
    @Provides
    fun provideRepresentanteDao(database: AppDatabase) = database.representanteDao()

    // DAOs para Vistas
    @Singleton
    @Provides
    fun providePacienteConCitaDao(database: AppDatabase) = database.pacienteConCitaDao()

    @Singleton
    @Provides
    fun providePacienteConConsultaYDetallesDao(database: AppDatabase) = database.pacienteConConsultaYDetallesDao()

    @Singleton
    @Provides
    fun providePerfilInstitucionalDao(database: AppDatabase) = database.perfilInstitucionalDao()

    @Singleton
    @Provides
    fun provideHistorialMedicoDao(database: AppDatabase) = database.historialMedicoDao()
}