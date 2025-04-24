package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.LoginSegenRepository
import com.nutrizulia.data.repository.ParroquiaRepository
import com.nutrizulia.domain.model.Parroquia
import javax.inject.Inject

class GetParroquias @Inject constructor(
    private val repository: ParroquiaRepository,
    private val loginRepository: LoginSegenRepository
) {

    suspend operator fun invoke(codEntidad: String, codMunicipio: String): List<Parroquia> {
        var parroquias = repository.getParroquias(codEntidad, codMunicipio)
        if (parroquias.isEmpty()) {
            val token = loginRepository.loginSegen().token
            if (token != null) {
                parroquias = repository.getParroquiasFromApi(token, codEntidad, codMunicipio)
                repository.insertParroquias(parroquias)
            }
        }
        return parroquias
    }
}