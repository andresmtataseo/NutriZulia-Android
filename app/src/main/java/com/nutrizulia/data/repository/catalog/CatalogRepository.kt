package com.nutrizulia.data.repository.catalog

import com.nutrizulia.data.local.dao.catalog.*
import com.nutrizulia.data.local.dao.user.InstitucionDao
import com.nutrizulia.data.local.dao.user.RolDao
import com.nutrizulia.data.local.dao.user.UsuarioInstitucionDao
import com.nutrizulia.data.local.entity.catalog.VersionEntity
import com.nutrizulia.util.TokenManager
import com.nutrizulia.data.remote.api.catalog.CatalogService
import com.nutrizulia.data.remote.dto.catalog.VersionResponseDto
import com.nutrizulia.data.remote.dto.catalog.toEntity
import com.nutrizulia.data.remote.dto.user.toEntity
import com.nutrizulia.util.JwtUtils
import com.nutrizulia.util.SyncResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CatalogRepository @Inject constructor(
    private val service: CatalogService,
    private val versionDao: VersionDao,
    private val rolDao: RolDao,
    private val etniaDao: EtniaDao,
    private val enfermedadDao: EnfermedadDao,
    private val nacionalidadDao: NacionalidadDao,
    private val estadoDao: EstadoDao,
    private val municipioDao: MunicipioDao,
    private val parroquiaDao: ParroquiaDao,
    private val especialidadDao: EspecialidadDao,
    private val grupoEtarioDao: GrupoEtarioDao,
    private val municipioSanitarioDao: MunicipioSanitarioDao,
    private val parametroCrecimientoNinosEdadDao: ParametroCrecimientoNinoEdadDao,
    private val parametroCrecimientoPediatricosEdadDao: ParametroCrecimientoPediatricoEdadDao,
    private val parametroCrecimientoPediatricosLongitudDao: ParametroCrecimientoPediatricoLongitudDao,
    private val parentescoDao: ParentescoDao,
    private val regexDao: RegexDao,
    private val reglaInterpretacionImcDao: ReglaInterpretacionImcDao,
    private val reglaInterpretacionPercentilDao: ReglaInterpretacionPercentilDao,
    private val reglaInterpretacionZScoreDao: ReglaInterpretacionZScoreDao,
    private val riesgoBiologicoDao: RiesgoBiologicoDao,
    private val tipoActividadDao: TipoActividadDao,
    private val tipoIndicadorDao: TipoIndicadorDao,
    private val tipoInstitucionDao: TipoInstitucionDao,
    private val institucionDao: InstitucionDao,
  //  private val usuarioInstitucionDao: UsuarioInstitucionDao,
    private val tokenManager: TokenManager,
    ) {

    suspend fun syncAllCatalogs(): SyncResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val versionResponse = service.getVersion()
            if (!versionResponse.isSuccessful) {
                return@withContext SyncResult(0, false, "Error al obtener versiones del backend")
            }

            val versiones = versionResponse.body().orEmpty()
            var totalInsertados = 0

            // Lote 1: catálogos independientes
            val lote1 = setOf(
                "roles", "etnias", "enfermedades", "nacionalidades", "especialidades",
                "grupos_etarios", "parentescos", "regex",
                "riesgos_biologicos", "tipos_actividades", "tipos_indicadores", "tipos_instituciones"
            )

            for (catalogo in versiones.filter { it.nombreTabla in lote1 }) {
                totalInsertados += when (catalogo.nombreTabla) {
                    "roles" -> syncIfNeeded(catalogo, { service.getRoles().body().orEmpty() }, { it.toEntity() }, rolDao::insertAll)
                    "etnias" -> syncIfNeeded(catalogo, { service.getEtnias().body().orEmpty() }, { it.toEntity() }, etniaDao::insertAll)
                    "enfermedades" -> syncIfNeeded(catalogo, { service.getEnfermedades().body().orEmpty() }, { it.toEntity() }, enfermedadDao::insertAll)
                    "nacionalidades" -> syncIfNeeded(catalogo, { service.getNacionalidades().body().orEmpty() }, { it.toEntity() }, nacionalidadDao::insertAll)
                    "especialidades" -> syncIfNeeded(catalogo, { service.getEspecialidades().body().orEmpty() }, { it.toEntity() }, especialidadDao::insertAll)
                    "grupos_etarios" -> syncIfNeeded(catalogo, { service.getGruposEtarios().body().orEmpty() }, { it.toEntity() }, grupoEtarioDao::insertAll)
                    "parentescos" -> syncIfNeeded(catalogo, { service.getParentescos().body().orEmpty() }, { it.toEntity() }, parentescoDao::insertAll)
                    "regex" -> syncIfNeeded(catalogo, { service.getRegex().body().orEmpty() }, { it.toEntity() }, regexDao::insertAll)
                    "riesgos_biologicos" -> syncIfNeeded(catalogo, { service.getRiesgosBiologicos().body().orEmpty() }, { it.toEntity() }, riesgoBiologicoDao::insertAll)
                    "tipos_actividades" -> syncIfNeeded(catalogo, { service.getTiposActividades().body().orEmpty() }, { it.toEntity() }, tipoActividadDao::insertAll)
                    "tipos_indicadores" -> syncIfNeeded(catalogo, { service.getTiposIndicadores().body().orEmpty() }, { it.toEntity() }, tipoIndicadorDao::insertAll)
                    "tipos_instituciones" -> syncIfNeeded(catalogo, { service.getTiposInstituciones().body().orEmpty() }, { it.toEntity() }, tipoInstitucionDao::insertAll)
                    else -> 0
                }
            }

            // Lote 2: catálogos con dependencias
            val lote2 = setOf(
                "parametros_crecimientos_ninos_edad",
                "parametros_crecimientos_pediatricos_edad",
                "parametros_crecimientos_pediatricos_longitud",
                "reglas_interpretaciones_imc",
                "reglas_interpretaciones_percentil",
                "reglas_interpretaciones_z_score"
            )

            for (catalogo in versiones.filter { it.nombreTabla in lote2 }) {
                totalInsertados += when (catalogo.nombreTabla) {
                    "parametros_crecimientos_ninos_edad" -> syncIfNeeded(catalogo, { service.getParametrosCrecimientosNinosEdad().body().orEmpty() }, { it.toEntity() }, parametroCrecimientoNinosEdadDao::insertAll)
                    "parametros_crecimientos_pediatricos_edad" -> syncIfNeeded(catalogo, { service.getParametrosCrecimientosPedriaticoEdad().body().orEmpty() }, { it.toEntity() }, parametroCrecimientoPediatricosEdadDao::insertAll)
                    "parametros_crecimientos_pediatricos_longitud" -> syncIfNeeded(catalogo, { service.getParametrosCrecimientosPedriaticoLongitud().body().orEmpty() }, { it.toEntity() }, parametroCrecimientoPediatricosLongitudDao::insertAll)
                    "reglas_interpretaciones_imc" -> syncIfNeeded(catalogo, { service.getReglasInterpretacionesImc().body().orEmpty() }, { it.toEntity() }, reglaInterpretacionImcDao::insertAll)
                    "reglas_interpretaciones_percentil" -> syncIfNeeded(catalogo, { service.getReglasInterpretacionesPercentil().body().orEmpty() }, { it.toEntity() }, reglaInterpretacionPercentilDao::insertAll)
                    "reglas_interpretaciones_z_score" -> syncIfNeeded(catalogo, { service.getReglasInterpretacionesZScore().body().orEmpty() }, { it.toEntity() }, reglaInterpretacionZScoreDao::insertAll)
                    else -> 0
                }
            }

            // Lote 3: estados
            versiones.find { it.nombreTabla == "estados" }?.let { catalogo ->
                totalInsertados += syncIfNeeded(catalogo, { service.getEstados().body().orEmpty() }, { it.toEntity() }, estadoDao::insertAll)
            }

            // Lote 4: municipios y municipios_sanitarios
            val estados = estadoDao.findAll()
            versiones.find { it.nombreTabla == "municipios" }?.let { catalogo ->
                for (estado in estados) {
                    val municipios = service.getMunicipios(estado.id).body().orEmpty()
                    municipioDao.insertAll(municipios.map { it.toEntity() })
                    totalInsertados += municipios.size
                }
                updateVersion(catalogo)
            }
            versiones.find { it.nombreTabla == "municipios_sanitarios" }?.let { catalogo ->
                for (estado in estados) {
                    val municipiosSanitarios = service.getMunicipiosSanitarios(estado.id).body().orEmpty()
                    municipioSanitarioDao.insertAll(municipiosSanitarios.map { it.toEntity() })
                    totalInsertados += municipiosSanitarios.size
                }
                updateVersion(catalogo)
            }

            // Lote 5: parroquias
            versiones.find { it.nombreTabla == "parroquias" }?.let { catalogo ->
                for (estado in estados) {
                    val municipios = municipioDao.findAllByEstadoId(estado.id)
                    for (municipio in municipios) {
                        val parroquias = service.getParroquias(estado.id, municipio.id).body().orEmpty()
                        parroquiaDao.insertAll(parroquias.map { it.toEntity() })
                        totalInsertados += parroquias.size
                    }
                }
                updateVersion(catalogo)
            }

            // buscar el id usuario
            val token = tokenManager.getToken()
            val usuarioId = JwtUtils.extractIdUsuario(token) ?: return@withContext SyncResult(0, false, "Error al obtener el ID del usuario")

            // Lote 3: Instituciones
            val lote3 = setOf(
                "instituciones" //, "usuarios_instituciones"
            )

            for (catalogo in versiones.filter { it.nombreTabla in lote3 }) {
                totalInsertados += when (catalogo.nombreTabla) {
                    "instituciones" -> syncIfNeeded(catalogo, { service.getInstituciones().body().orEmpty() }, { it.toEntity() }, institucionDao::insertAll)
                   //usuario_instituciones no es dentro de la tabla version, hay que hacerlo, quizas en la autenticacion, primero autentica, sincroniza catalog y luego crea el usuario_institucion
                   // "usuarios_instituciones" -> syncIfNeeded(catalogo, { service.getUsuarioInstitucion(usuarioId).body().orEmpty() }, { it.toEntity() }, usuarioInstitucionDao::insertAll)
                    else -> 0
                }
            }


            SyncResult(totalInsertados, true, "Sincronización completada con éxito")

        } catch (e: Exception) {
            SyncResult(0, false, "Error al sincronizar catálogos: ${e.message}")
        }
    }

    private suspend fun <T, E> syncIfNeeded(
        versionRemote: VersionResponseDto,
        fetchRemote: suspend () -> List<T>,
        toEntity: (T) -> E,
        insertAll: suspend (List<E>) -> Unit
    ): Int {
        val localVersion = versionDao.findByNombre(versionRemote.nombreTabla)?.version ?: 0
        if (localVersion >= versionRemote.version) return 0

        val remoteList = fetchRemote()
        val entities = remoteList.map(toEntity)
        insertAll(entities)

        updateVersion(versionRemote)
        return entities.size
    }

    private suspend fun updateVersion(versionRemote: VersionResponseDto) {
        versionDao.insert(
            VersionEntity(
                nombreTabla = versionRemote.nombreTabla,
                version = versionRemote.version
            )
        )
    }
}
