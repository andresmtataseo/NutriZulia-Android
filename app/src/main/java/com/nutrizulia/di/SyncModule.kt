package com.nutrizulia.di

import com.nutrizulia.data.local.AppDatabase
import com.nutrizulia.data.local.dao.catalog.*
import com.nutrizulia.data.local.dao.user.InstitucionDao
import com.nutrizulia.data.local.dao.user.RolDao
import com.nutrizulia.data.remote.api.catalog.CatalogService
import com.nutrizulia.data.remote.dto.catalog.toEntity
import com.nutrizulia.data.repository.catalog.sync.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {

    private fun <DTO, ENTITY> createBaseSyncer(
        tableName: String,
        versionDao: VersionDao,
        database: AppDatabase,
        serviceCall: suspend () -> List<DTO>,
        syncOperation: suspend (List<ENTITY>) -> Unit,
        mapper: (DTO) -> ENTITY
    ): CatalogSyncer {
        return BaseCatalogSyncer(
            tableName = tableName,
            versionDao = versionDao,
            database = database,
            fetchRemoteData = serviceCall,
            syncOperation = syncOperation,
            mapToEntity = mapper
        )
    }

    // --- Independent Catalogs ---
    @Provides @Singleton @IntoMap @StringKey("roles")
    fun provideRolSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: RolDao) = createBaseSyncer("roles", v, db, { s.getRoles().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("etnias")
    fun provideEtniaSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: EtniaDao) = createBaseSyncer("etnias", v, db, { s.getEtnias().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("enfermedades")
    fun provideEnfermedadSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: EnfermedadDao) = createBaseSyncer("enfermedades", v, db, { s.getEnfermedades().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("nacionalidades")
    fun provideNacionalidadSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: NacionalidadDao) = createBaseSyncer("nacionalidades", v, db, { s.getNacionalidades().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("especialidades")
    fun provideEspecialidadSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: EspecialidadDao) = createBaseSyncer("especialidades", v, db, { s.getEspecialidades().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("grupos_etarios")
    fun provideGrupoEtarioSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: GrupoEtarioDao) = createBaseSyncer("grupos_etarios", v, db, { s.getGruposEtarios().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("parentescos")
    fun provideParentescoSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: ParentescoDao) = createBaseSyncer("parentescos", v, db, { s.getParentescos().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("regex")
    fun provideRegexSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: RegexDao) = createBaseSyncer("regex", v, db, { s.getRegex().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("riesgos_biologicos")
    fun provideRiesgoBiologicoSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: RiesgoBiologicoDao) = createBaseSyncer("riesgos_biologicos", v, db, { s.getRiesgosBiologicos().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("tipos_actividades")
    fun provideTipoActividadSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: TipoActividadDao) = createBaseSyncer("tipos_actividades", v, db, { s.getTiposActividades().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("tipos_indicadores")
    fun provideTipoIndicadorSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: TipoIndicadorDao) = createBaseSyncer("tipos_indicadores", v, db, { s.getTiposIndicadores().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("tipos_instituciones")
    fun provideTipoInstitucionSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: TipoInstitucionDao) = createBaseSyncer("tipos_instituciones", v, db, { s.getTiposInstituciones().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    // --- Catalogs with dependencies or special parameters ---
    @Provides @Singleton @IntoMap @StringKey("parametros_crecimientos_ninos_edad")
    fun provideParamCrecimientoNinoEdadSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: ParametroCrecimientoNinoEdadDao) = createBaseSyncer("parametros_crecimientos_ninos_edad", v, db, { s.getParametrosCrecimientosNinosEdad().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("parametros_crecimientos_pediatricos_edad")
    fun provideParamCrecimientoPediatricoEdadSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: ParametroCrecimientoPediatricoEdadDao) = createBaseSyncer("parametros_crecimientos_pediatricos_edad", v, db, { s.getParametrosCrecimientosPediatricoEdad().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("parametros_crecimientos_pediatricos_longitud")
    fun provideParamCrecimientoPediatricoLongitudSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: ParametroCrecimientoPediatricoLongitudDao) = createBaseSyncer("parametros_crecimientos_pediatricos_longitud", v, db, { s.getParametrosCrecimientosPediatricoLongitud().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("reglas_interpretaciones_imc")
    fun provideReglaImcSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: ReglaInterpretacionImcDao) = createBaseSyncer("reglas_interpretaciones_imc", v, db, { s.getReglasInterpretacionesImc().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("reglas_interpretaciones_percentil")
    fun provideReglaPercentilSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: ReglaInterpretacionPercentilDao) = createBaseSyncer("reglas_interpretaciones_percentil", v, db, { s.getReglasInterpretacionesPercentil().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("reglas_interpretaciones_z_score")
    fun provideReglaZScoreSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: ReglaInterpretacionZScoreDao) = createBaseSyncer("reglas_interpretaciones_z_score", v, db, { s.getReglasInterpretacionesZScore().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    // --- Location Catalogs (require specific order) ---
    // Estos también se benefician de la estrategia upsert.
    @Provides @Singleton @IntoMap @StringKey("estados")
    fun provideEstadoSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: EstadoDao) = createBaseSyncer("estados", v, db, { s.getEstados().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    // Los syncers personalizados no cambian, ya que su lógica de borrado es interna y más controlada.
    @Provides @Singleton @IntoMap @StringKey("municipios")
    fun provideMunicipioSyncer(syncer: MunicipioSyncer): CatalogSyncer = syncer

    @Provides @Singleton @IntoMap @StringKey("municipios_sanitarios")
    fun provideMunicipioSanitarioSyncer(syncer: MunicipioSanitarioSyncer): CatalogSyncer = syncer

    @Provides @Singleton @IntoMap @StringKey("parroquias")
    fun provideParroquiaSyncer(syncer: ParroquiaSyncer): CatalogSyncer = syncer

    // -- Institucion
    @Provides @Singleton @IntoMap @StringKey("instituciones")
    fun provideInstitucionSyncer(s: CatalogService, v: VersionDao, db: AppDatabase, d: InstitucionDao) = createBaseSyncer("instituciones", v, db, { s.getInstituciones().body()?.data.orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

}