package com.nutrizulia.data.remote.api.catalog

import com.nutrizulia.data.remote.dto.catalog.*
import retrofit2.Response
import javax.inject.Inject

class CatalogService @Inject constructor(
    private val api: ICatalogService
) {

    suspend fun getTiposInstituciones(): Response<List<TipoInstitucionResponseDto>> {
        return api.getTiposInstituciones()
    }

    suspend fun getTiposIndicadores(): Response<List<TipoIndicadorResponseDto>> {
        return api.getTiposIndicadores()
    }

    suspend fun getTiposActividades(): Response<List<TipoActividadResponseDto>> {
        return api.getTiposActividades()
    }

    suspend fun getRiesgosBiologicos(): Response<List<RiesgoBiologicoResponseDto>> {
        return api.getRiesgosBiologicos()
    }

    suspend fun getReglasInterpretacionesZScore(): Response<List<ReglaInterpretacionZScoreResponseDto>> {
        return api.getReglasInterpretacionesZScore()
    }

    suspend fun getReglasInterpretacionesPercentil(): Response<List<ReglaInterpretacionPercentilResponseDto>> {
        return api.getReglasInterpretacionesPercentil()
    }

    suspend fun getReglasInterpretacionesImc(): Response<List<ReglaInterpretacionImcResponseDto>> {
        return api.getReglasInterpretacionesImc()
    }

    suspend fun getParroquias(idEstado: Int, idMunicipio: Int): Response<List<ParroquiaResponseDto>> {
        return api.getParroquias(idEstado, idMunicipio)
    }

    suspend fun getParentescos(): Response<List<ParentescoResponseDto>> {
        return api.getParentescos()
    }

    suspend fun getParametrosCrecimientosPedriaticoLongitud(): Response<List<ParametroCrecimientoPediatricoLongitudResponseDto>> {
        return api.getParametrosCrecimientosPedriaticoLongitud()
    }

    suspend fun getParametrosCrecimientosPedriaticoEdad(): Response<List<ParametroCrecimientoPediatricoEdadResponseDto>> {
        return api.getParametrosCrecimientosPedriaticoEdad()
    }

    suspend fun getParametrosCrecimientosNinosEdad(): Response<List<ParametroCrecimientoNinoEdadResponseDto>> {
        return api.getParametrosCrecimientosNinosEdad()
    }

    suspend fun getNacionalidades(): Response<List<NacionalidadResponseDto>> {
        return api.getNacionalidades()
    }

    suspend fun getMunicipios(idEstado: Int): Response<List<MunicipioResponseDto>> {
        return api.getMunicipios(idEstado)
    }

    suspend fun getMunicipiosSanitarios(idEstado: Int): Response<List<MunicipioSanitarioResponseDto>> {
        return api.getMunicipiosSanitarios(idEstado)
    }

    suspend fun getGruposEtarios(): Response<List<GrupoEtarioResponseDto>> {
        return api.getGruposEtarios()
    }

    suspend fun getEtnias(): Response<List<EtniaResponseDto>> {
        return api.getEtnias()
    }

    suspend fun getEstados(): Response<List<EstadoResponseDto>> {
        return api.getEstados()
    }

    suspend fun getEspecialidades(): Response<List<EspecialidadResponseDto>> {
        return api.getEspecialidades()
    }

    suspend fun getEnfermedades(): Response<List<EnfermedadResponseDto>> {
        return api.getEnfermedades()
    }

    suspend fun getVersion(): Response<List<VersionResponseDto>> {
        return api.getVersion()
    }

    suspend fun getRegex(): Response<List<RegexResponseDto>> {
        return api.getRegex()
    }

}
