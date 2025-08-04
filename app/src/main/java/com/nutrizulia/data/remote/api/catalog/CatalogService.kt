package com.nutrizulia.data.remote.api.catalog

import com.nutrizulia.data.remote.dto.ApiResponseDto
import com.nutrizulia.data.remote.dto.catalog.*
import com.nutrizulia.data.remote.dto.user.UsuarioInstitucionResponseDto
import retrofit2.Response
import javax.inject.Inject

class CatalogService @Inject constructor(
    private val api: ICatalogService
) {

    suspend fun getTiposInstituciones(): Response<ApiResponseDto<List<TipoInstitucionResponseDto>>> {
        return api.getTiposInstituciones()
    }

    suspend fun getTiposIndicadores(): Response<ApiResponseDto<List<TipoIndicadorResponseDto>>> {
        return api.getTiposIndicadores()
    }

    suspend fun getTiposActividades(): Response<ApiResponseDto<List<TipoActividadResponseDto>>> {
        return api.getTiposActividades()
    }

    suspend fun getRiesgosBiologicos(): Response<ApiResponseDto<List<RiesgoBiologicoResponseDto>>> {
        return api.getRiesgosBiologicos()
    }

    suspend fun getReglasInterpretacionesZScore(): Response<ApiResponseDto<List<ReglaInterpretacionZScoreResponseDto>>> {
        return api.getReglasInterpretacionesZScore()
    }

    suspend fun getReglasInterpretacionesPercentil(): Response<ApiResponseDto<List<ReglaInterpretacionPercentilResponseDto>>> {
        return api.getReglasInterpretacionesPercentil()
    }

    suspend fun getReglasInterpretacionesImc(): Response<ApiResponseDto<List<ReglaInterpretacionImcResponseDto>>> {
        return api.getReglasInterpretacionesImc()
    }

    suspend fun getParroquias(idEstado: Int, idMunicipio: Int): Response<ApiResponseDto<List<ParroquiaResponseDto>>> {
        return api.getParroquias(idEstado, idMunicipio)
    }

    suspend fun getParentescos(): Response<ApiResponseDto<List<ParentescoResponseDto>>> {
        return api.getParentescos()
    }

    suspend fun getParametrosCrecimientosPediatricoLongitud(): Response<ApiResponseDto<List<ParametroCrecimientoPediatricoLongitudResponseDto>>> {
        return api.getParametrosCrecimientosPediatricoLongitud()
    }

    suspend fun getParametrosCrecimientosPediatricoEdad(): Response<ApiResponseDto<List<ParametroCrecimientoPediatricoEdadResponseDto>>> {
        return api.getParametrosCrecimientosPediatricoEdad()
    }

    suspend fun getParametrosCrecimientosNinosEdad(): Response<ApiResponseDto<List<ParametroCrecimientoNinoEdadResponseDto>>> {
        return api.getParametrosCrecimientosNinosEdad()
    }

    suspend fun getNacionalidades(): Response<ApiResponseDto<List<NacionalidadResponseDto>>> {
        return api.getNacionalidades()
    }

    suspend fun getMunicipios(idEstado: Int): Response<ApiResponseDto<List<MunicipioResponseDto>>> {
        return api.getMunicipios(idEstado)
    }

    suspend fun getMunicipiosSanitarios(idEstado: Int): Response<ApiResponseDto<List<MunicipioSanitarioResponseDto>>> {
        return api.getMunicipiosSanitarios(idEstado)
    }

    suspend fun getGruposEtarios(): Response<ApiResponseDto<List<GrupoEtarioResponseDto>>> {
        return api.getGruposEtarios()
    }

    suspend fun getEtnias(): Response<ApiResponseDto<List<EtniaResponseDto>>> {
        return api.getEtnias()
    }

    suspend fun getEstados(): Response<ApiResponseDto<List<EstadoResponseDto>>> {
        return api.getEstados()
    }

    suspend fun getEspecialidades(): Response<ApiResponseDto<List<EspecialidadResponseDto>>> {
        return api.getEspecialidades()
    }

    suspend fun getEnfermedades(): Response<ApiResponseDto<List<EnfermedadResponseDto>>> {
        return api.getEnfermedades()
    }

    suspend fun getVersion(): Response<ApiResponseDto<List<VersionResponseDto>>> {
        return api.getVersiones()
    }

    suspend fun getRegex(): Response<ApiResponseDto<List<RegexResponseDto>>> {
        return api.getRegex()
    }

    suspend fun getRoles(): Response<ApiResponseDto<List<RolResponseDto>>> {
        return api.getRoles()
    }

    suspend fun getInstituciones(): Response<ApiResponseDto<List<InstitucionResponseDto>>> {
        return api.getInstituciones()
    }

    suspend fun getUsuarioInstitucion(idUsuario: Int): Response<ApiResponseDto<List<UsuarioInstitucionResponseDto>>> {
        return api.getUsuarioInstitucion(idUsuario)
    }

}
