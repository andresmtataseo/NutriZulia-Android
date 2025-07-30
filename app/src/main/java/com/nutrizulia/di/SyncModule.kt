package com.nutrizulia.di

import com.nutrizulia.data.local.dao.catalog.*
import com.nutrizulia.data.local.dao.collection.*
import com.nutrizulia.data.local.dao.user.InstitucionDao
import com.nutrizulia.data.local.dao.user.RolDao
import com.nutrizulia.data.remote.api.catalog.CatalogService
import com.nutrizulia.data.remote.api.sync.ISyncService
import com.nutrizulia.data.remote.dto.catalog.toEntity
import com.nutrizulia.data.repository.catalog.sync.*
import com.nutrizulia.data.repository.sync.SyncRepository
import com.nutrizulia.util.SyncManager
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
        serviceCall: suspend () -> List<DTO>,
        syncOperation: suspend (List<ENTITY>) -> Unit,
        mapper: (DTO) -> ENTITY
    ): CatalogSyncer {
        return BaseCatalogSyncer(
            tableName = tableName,
            versionDao = versionDao,
            fetchRemoteData = serviceCall,
            syncOperation = syncOperation,
            mapToEntity = mapper
        )
    }

    // --- Independent Catalogs ---
    @Provides @Singleton @IntoMap @StringKey("roles")
    fun provideRolSyncer(s: CatalogService, v: VersionDao, d: RolDao) = createBaseSyncer("roles", v, { s.getRoles().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("etnias")
    fun provideEtniaSyncer(s: CatalogService, v: VersionDao, d: EtniaDao) = createBaseSyncer("etnias", v, { s.getEtnias().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("enfermedades")
    fun provideEnfermedadSyncer(s: CatalogService, v: VersionDao, d: EnfermedadDao) = createBaseSyncer("enfermedades", v, { s.getEnfermedades().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("nacionalidades")
    fun provideNacionalidadSyncer(s: CatalogService, v: VersionDao, d: NacionalidadDao) = createBaseSyncer("nacionalidades", v, { s.getNacionalidades().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("especialidades")
    fun provideEspecialidadSyncer(s: CatalogService, v: VersionDao, d: EspecialidadDao) = createBaseSyncer("especialidades", v, { s.getEspecialidades().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("grupos_etarios")
    fun provideGrupoEtarioSyncer(s: CatalogService, v: VersionDao, d: GrupoEtarioDao) = createBaseSyncer("grupos_etarios", v, { s.getGruposEtarios().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("parentescos")
    fun provideParentescoSyncer(s: CatalogService, v: VersionDao, d: ParentescoDao) = createBaseSyncer("parentescos", v, { s.getParentescos().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("regex")
    fun provideRegexSyncer(s: CatalogService, v: VersionDao, d: RegexDao) = createBaseSyncer("regex", v, { s.getRegex().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("riesgos_biologicos")
    fun provideRiesgoBiologicoSyncer(s: CatalogService, v: VersionDao, d: RiesgoBiologicoDao) = createBaseSyncer("riesgos_biologicos", v, { s.getRiesgosBiologicos().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("tipos_actividades")
    fun provideTipoActividadSyncer(s: CatalogService, v: VersionDao, d: TipoActividadDao) = createBaseSyncer("tipos_actividades", v, { s.getTiposActividades().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("tipos_indicadores")
    fun provideTipoIndicadorSyncer(s: CatalogService, v: VersionDao, d: TipoIndicadorDao) = createBaseSyncer("tipos_indicadores", v, { s.getTiposIndicadores().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("tipos_instituciones")
    fun provideTipoInstitucionSyncer(s: CatalogService, v: VersionDao, d: TipoInstitucionDao) = createBaseSyncer("tipos_instituciones", v, { s.getTiposInstituciones().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    // --- Catalogs with dependencies or special parameters ---
    @Provides @Singleton @IntoMap @StringKey("parametros_crecimientos_ninos_edad")
    fun provideParamCrecimientoNinoEdadSyncer(s: CatalogService, v: VersionDao, d: ParametroCrecimientoNinoEdadDao) = createBaseSyncer("parametros_crecimientos_ninos_edad", v, { s.getParametrosCrecimientosNinosEdad().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("parametros_crecimientos_pediatricos_edad")
    fun provideParamCrecimientoPediatricoEdadSyncer(s: CatalogService, v: VersionDao, d: ParametroCrecimientoPediatricoEdadDao) = createBaseSyncer("parametros_crecimientos_pediatricos_edad", v, { s.getParametrosCrecimientosPedriaticoEdad().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("parametros_crecimientos_pediatricos_longitud")
    fun provideParamCrecimientoPediatricoLongitudSyncer(s: CatalogService, v: VersionDao, d: ParametroCrecimientoPediatricoLongitudDao) = createBaseSyncer("parametros_crecimientos_pediatricos_longitud", v, { s.getParametrosCrecimientosPedriaticoLongitud().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("reglas_interpretaciones_imc")
    fun provideReglaImcSyncer(s: CatalogService, v: VersionDao, d: ReglaInterpretacionImcDao) = createBaseSyncer("reglas_interpretaciones_imc", v, { s.getReglasInterpretacionesImc().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("reglas_interpretaciones_percentil")
    fun provideReglaPercentilSyncer(s: CatalogService, v: VersionDao, d: ReglaInterpretacionPercentilDao) = createBaseSyncer("reglas_interpretaciones_percentil", v, { s.getReglasInterpretacionesPercentil().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    @Provides @Singleton @IntoMap @StringKey("reglas_interpretaciones_z_score")
    fun provideReglaZScoreSyncer(s: CatalogService, v: VersionDao, d: ReglaInterpretacionZScoreDao) = createBaseSyncer("reglas_interpretaciones_z_score", v, { s.getReglasInterpretacionesZScore().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    // --- Location Catalogs (require specific order) ---
    // Estos también se benefician de la estrategia upsert.
    @Provides @Singleton @IntoMap @StringKey("estados")
    fun provideEstadoSyncer(s: CatalogService, v: VersionDao, d: EstadoDao) = createBaseSyncer("estados", v, { s.getEstados().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    // Los syncers personalizados no cambian, ya que su lógica de borrado es interna y más controlada.
    @Provides @Singleton @IntoMap @StringKey("municipios")
    fun provideMunicipioSyncer(syncer: MunicipioSyncer): CatalogSyncer = syncer

    @Provides @Singleton @IntoMap @StringKey("municipios_sanitarios")
    fun provideMunicipioSanitarioSyncer(syncer: MunicipioSanitarioSyncer): CatalogSyncer = syncer

    @Provides @Singleton @IntoMap @StringKey("parroquias")
    fun provideParroquiaSyncer(syncer: ParroquiaSyncer): CatalogSyncer = syncer

    // -- Institucion
    @Provides @Singleton @IntoMap @StringKey("instituciones")
    fun provideInstitucionSyncer(s: CatalogService, v: VersionDao, d: InstitucionDao) = createBaseSyncer("instituciones", v, { s.getInstituciones().body().orEmpty() }, { d.upsertAll(it) }, { it.toEntity() })

    // -- SyncRepository para sincronización bidireccional
    @Provides
    @Singleton
    fun provideSyncRepository(
        syncService: ISyncService,
        syncManager: SyncManager,
        pacienteDao: PacienteDao,
        consultaDao: ConsultaDao,
        representanteDao: RepresentanteDao,
        pacienteRepresentanteDao: PacienteRepresentanteDao,
        evaluacionAntropometricaDao: EvaluacionAntropometricaDao,
        detallePediatricoDao: DetallePediatricoDao,
        detalleMetabolicoDao: DetalleMetabolicoDao,
        detalleVitalDao: DetalleVitalDao,
        detalleAntropometricoDao: DetalleAntropometricoDao,
        detalleObstetriciaDao: DetalleObstetriciaDao,
        diagnosticoDao: DiagnosticoDao,
        actividadDao: ActividadDao
    ): SyncRepository {
        return SyncRepository(
            syncService = syncService,
            syncManager = syncManager,
            pacienteDao = pacienteDao,
            consultaDao = consultaDao,
            representanteDao = representanteDao,
            pacienteRepresentanteDao = pacienteRepresentanteDao,
            evaluacionAntropometricaDao = evaluacionAntropometricaDao,
            detallePediatricoDao = detallePediatricoDao,
            detalleMetabolicoDao = detalleMetabolicoDao,
            detalleVitalDao = detalleVitalDao,
            detalleAntropometricoDao = detalleAntropometricoDao,
            detalleObstetriciaDao = detalleObstetriciaDao,
            diagnosticoDao = diagnosticoDao,
            actividadDao = actividadDao
        )
    }

}