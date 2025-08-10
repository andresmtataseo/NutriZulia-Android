package com.nutrizulia.data.remote.api.catalog

import com.nutrizulia.data.remote.dto.ApiResponseDto
import com.nutrizulia.data.remote.dto.catalog.*
import com.nutrizulia.data.remote.dto.user.UsuarioInstitucionResponseDto
import com.nutrizulia.util.CatalogEndpoints
import com.nutrizulia.util.UserInstitutionEndpoints
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ICatalogService {

    @GET(CatalogEndpoints.INSTITUTION_TYPES)
    suspend fun getTiposInstituciones(): Response<ApiResponseDto<List<TipoInstitucionResponseDto>>>

    @GET(CatalogEndpoints.INDICATOR_TYPES)
    suspend fun getTiposIndicadores(): Response<ApiResponseDto<List<TipoIndicadorResponseDto>>>

    @GET(CatalogEndpoints.ACTIVITY_TYPES)
    suspend fun getTiposActividades(): Response<ApiResponseDto<List<TipoActividadResponseDto>>>

    @GET(CatalogEndpoints.BIOLOGICAL_RISKS)
    suspend fun getRiesgosBiologicos(): Response<ApiResponseDto<List<RiesgoBiologicoResponseDto>>>

    @GET(CatalogEndpoints.Z_SCORE_INTERPRETATION_RULES)
    suspend fun getReglasInterpretacionesZScore(): Response<ApiResponseDto<List<ReglaInterpretacionZScoreResponseDto>>>

    @GET(CatalogEndpoints.PERCENTILE_INTERPRETATION_RULES)
    suspend fun getReglasInterpretacionesPercentil(): Response<ApiResponseDto<List<ReglaInterpretacionPercentilResponseDto>>>

    @GET(CatalogEndpoints.BMI_INTERPRETATION_RULES)
    suspend fun getReglasInterpretacionesImc(): Response<ApiResponseDto<List<ReglaInterpretacionImcResponseDto>>>

    @GET(CatalogEndpoints.PARISHES)
    suspend fun getParroquias(
        @Query("idEstado") idEstado: Int,
        @Query("idMunicipio") idMunicipio: Int
    ): Response<ApiResponseDto<List<ParroquiaResponseDto>>>

    @GET(CatalogEndpoints.RELATIONSHIPS)
    suspend fun getParentescos(): Response<ApiResponseDto<List<ParentescoResponseDto>>>

    @GET(CatalogEndpoints.PEDIATRIC_LENGTH_PARAMETERS)
    suspend fun getParametrosCrecimientosPediatricoLongitud(): Response<ApiResponseDto<List<ParametroCrecimientoPediatricoLongitudResponseDto>>>

    @GET(CatalogEndpoints.PEDIATRIC_AGE_PARAMETERS)
    suspend fun getParametrosCrecimientosPediatricoEdad(): Response<ApiResponseDto<List<ParametroCrecimientoPediatricoEdadResponseDto>>>

    @GET(CatalogEndpoints.CHILDREN_AGE_PARAMETERS)
    suspend fun getParametrosCrecimientosNinosEdad(): Response<ApiResponseDto<List<ParametroCrecimientoNinoEdadResponseDto>>>

    @GET(CatalogEndpoints.NATIONALITIES)
    suspend fun getNacionalidades(): Response<ApiResponseDto<List<NacionalidadResponseDto>>>

    @GET(CatalogEndpoints.MUNICIPALITIES)
    suspend fun getMunicipios(
        @Query("idEstado") idEstado: Int
    ): Response<ApiResponseDto<List<MunicipioResponseDto>>>

    @GET(CatalogEndpoints.HEALTH_MUNICIPALITIES)
    suspend fun getMunicipiosSanitarios(
        @Query("idEstado") idEstado: Int
    ): Response<ApiResponseDto<List<MunicipioSanitarioResponseDto>>>

    @GET(CatalogEndpoints.AGE_GROUPS)
    suspend fun getGruposEtarios(): Response<ApiResponseDto<List<GrupoEtarioResponseDto>>>

    @GET(CatalogEndpoints.ETHNICITIES)
    suspend fun getEtnias(): Response<ApiResponseDto<List<EtniaResponseDto>>>

    @GET(CatalogEndpoints.STATES)
    suspend fun getEstados(): Response<ApiResponseDto<List<EstadoResponseDto>>>

    @GET(CatalogEndpoints.SPECIALTIES)
    suspend fun getEspecialidades(): Response<ApiResponseDto<List<EspecialidadResponseDto>>>

    @GET(CatalogEndpoints.DISEASES)
    suspend fun getEnfermedades(): Response<ApiResponseDto<List<EnfermedadResponseDto>>>

    @GET(CatalogEndpoints.VERSIONS)
    suspend fun getVersiones(): Response<ApiResponseDto<List<VersionResponseDto>>>

    @GET(CatalogEndpoints.REGEX)
    suspend fun getRegex(): Response<ApiResponseDto<List<RegexResponseDto>>>

    @GET(CatalogEndpoints.ROLES)
    suspend fun getRoles(): Response<ApiResponseDto<List<RolResponseDto>>>

    @GET(CatalogEndpoints.INSTITUTIONS)
    suspend fun getInstituciones(): Response<ApiResponseDto<List<InstitucionResponseDto>>>

    @GET(UserInstitutionEndpoints.GET_BY_USER)
    suspend fun getUsuarioInstitucion(
        @Query("idUsuario") idUsuario: Int
    ): Response<ApiResponseDto<List<UsuarioInstitucionResponseDto>>>
}