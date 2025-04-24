package com.nutrizulia.domain.usecase

import com.nutrizulia.data.repository.EntidadRepository
import com.nutrizulia.data.repository.LoginSegenRepository
import com.nutrizulia.domain.model.Entidad
import javax.inject.Inject

class GetEntidades @Inject constructor(
    private val repository: EntidadRepository,
    private val loginRepository: LoginSegenRepository
) {

    suspend operator fun invoke(): List<Entidad> {
        var entidades =  repository.getEntidades()
        if (entidades.isEmpty() ) {
            val token = loginRepository.loginSegen().token
            if (token != null) {
                entidades = repository.getEntidadesFromApi(token)
                repository.insertEntidades(entidades)
            }
        }
        return entidades
    }

}