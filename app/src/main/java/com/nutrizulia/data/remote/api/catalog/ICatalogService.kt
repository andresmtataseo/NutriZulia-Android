package com.nutrizulia.data.remote.api.catalog

import com.nutrizulia.data.remote.dto.catalog.*
import com.nutrizulia.data.remote.dto.user.UsuarioInstitucionResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ICatalogService {
    @GET("/catalog/v1/tiposInstituciones")
    suspend fun getTiposInstituciones(): Response<List<TipoInstitucionResponseDto>>

    @GET("/catalog/v1/tiposIndicadores")
    suspend fun getTiposIndicadores(): Response<List<TipoIndicadorResponseDto>>

    @GET("/catalog/v1/tiposActividades")
    suspend fun getTiposActividades(): Response<List<TipoActividadResponseDto>>

    @GET("/catalog/v1/riesgosBiologicos")
    suspend fun getRiesgosBiologicos(): Response<List<RiesgoBiologicoResponseDto>>

    @GET("/catalog/v1/reglasInterpretacionesZScore")
    suspend fun getReglasInterpretacionesZScore(): Response<List<ReglaInterpretacionZScoreResponseDto>>

    @GET("/catalog/v1/reglasInterpretacionesPercentil")
    suspend fun getReglasInterpretacionesPercentil(): Response<List<ReglaInterpretacionPercentilResponseDto>>

    @GET("/catalog/v1/reglasInterpretacionesImc")
    suspend fun getReglasInterpretacionesImc(): Response<List<ReglaInterpretacionImcResponseDto>>

    @GET("/catalog/v1/parroquias")
    suspend fun getParroquias(
        @Query("idEstado") idEstado: Int,
        @Query("idMunicipio") idMunicipio: Int
    ): Response<List<ParroquiaResponseDto>>

    @GET("/catalog/v1/parentescos")
    suspend fun getParentescos(): Response<List<ParentescoResponseDto>>

    @GET("/catalog/v1/parametrosCrecimientosPediatricoLongitud")
    suspend fun getParametrosCrecimientosPedriaticoLongitud(): Response<List<ParametroCrecimientoPediatricoLongitudResponseDto>>

    @GET("/catalog/v1/parametrosCrecimientosPediatricoEdad")
    suspend fun getParametrosCrecimientosPedriaticoEdad(): Response<List<ParametroCrecimientoPediatricoEdadResponseDto>>

    @GET("/catalog/v1/parametrosCrecimientosNinosEdad")
    suspend fun getParametrosCrecimientosNinosEdad(): Response<List<ParametroCrecimientoNinoEdadResponseDto>>

    @GET("/catalog/v1/nacionalidades")
    suspend fun getNacionalidades(): Response<List<NacionalidadResponseDto>>

    @GET("/catalog/v1/municipios")
    suspend fun getMunicipios(
        @Query("idEstado") idEstado: Int
    ): Response<List<MunicipioResponseDto>>

    @GET("/catalog/v1/municipiosSanitarios")
    suspend fun getMunicipiosSanitarios(
        @Query("idEstado") idEstado: Int
    ): Response<List<MunicipioSanitarioResponseDto>>

    @GET("/catalog/v1/gruposEtarios")
    suspend fun getGruposEtarios(): Response<List<GrupoEtarioResponseDto>>

    @GET("/catalog/v1/etnias")
    suspend fun getEtnias(): Response<List<EtniaResponseDto>>

    @GET("/catalog/v1/estados")
    suspend fun getEstados(): Response<List<EstadoResponseDto>>

    @GET("/catalog/v1/especialidades")
    suspend fun getEspecialidades(): Response<List<EspecialidadResponseDto>>

    @GET("/catalog/v1/enfermedades")
    suspend fun getEnfermedades(): Response<List<EnfermedadResponseDto>>

    @GET("/catalog/v1/versiones")
    suspend fun getVersion(): Response<List<VersionResponseDto>>

    @GET("/catalog/v1/regex")
    suspend fun getRegex(): Response<List<RegexResponseDto>>

    @GET("/catalog/v1/roles")
    suspend fun getRoles(): Response<List<RolResponseDto>>

    @GET("/institution/v1/findAll")
    suspend fun getInstituciones(): Response<List<InstitucionResponseDto>>

    @GET("/user-institution/v1/findAll")
    suspend fun getUsuarioInstitucion(
        @Query("idUsuario") idUsuario: Int
    ): Response<List<UsuarioInstitucionResponseDto>>
}