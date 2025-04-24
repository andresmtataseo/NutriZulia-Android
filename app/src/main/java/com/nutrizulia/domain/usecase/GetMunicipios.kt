package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.LoginSegenRepository
import com.nutrizulia.data.repository.MunicipioRepository
import com.nutrizulia.domain.model.Municipio
import javax.inject.Inject

class GetMunicipios @Inject constructor(
    private val repository: MunicipioRepository,
    private val loginRepository: LoginSegenRepository
) {

    suspend operator fun invoke(codEntidad: String): List<Municipio> {
        var municipios = repository.getMunicipios(codEntidad)
        if (municipios.isEmpty()) {
            val token = loginRepository.loginSegen().token
            if (token != null) {
                municipios = repository.getMunicipiosFromApi(token, codEntidad)
                repository.insertMunicipios(municipios)
            }
        }
        return municipios
    }

}