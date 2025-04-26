package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.ComunidadRepository
import com.nutrizulia.data.repository.LoginSegenRepository
import com.nutrizulia.domain.model.Comunidad
import javax.inject.Inject

class GetComunidades @Inject constructor(
    private val repository: ComunidadRepository,
    private val loginRepository: LoginSegenRepository
) {

    suspend operator fun invoke(codEntidad: String, codMunicipio: String, codParroquia: String): List<Comunidad> {
        var comunidades = repository.getComunidades(codEntidad, codMunicipio, codParroquia)
        if (comunidades.isEmpty()) {
            val token = loginRepository.loginSegen().token
            if (token != null) {
                comunidades = repository.getComunidadesFromApi(token, codEntidad, codMunicipio, codParroquia)
                repository.insertComunidades(comunidades)
            }
        }
        return comunidades
    }

}